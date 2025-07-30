package com.example.eat_together.global.websocket;

import com.example.eat_together.domain.chat.dto.ChatMessageRequestDto;
import com.example.eat_together.domain.chat.service.ChatService;
import com.example.eat_together.domain.chat.service.ChatUtil;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Component
public class ChatMessageHandler extends TextWebSocketHandler {

    static Map<Long, Set<WebSocketSession>> nowChattingRooms = new HashMap<>();
    private final ChatService chatService;
    private final ObjectMapper objectMapper;
    private final ChatUtil chatUtil;

    //WebSocket 연결 시 동작
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        Long userId = extractLoginId(session);
        Long roomId = extractRoomId(session);

        if(!chatUtil.isGroupMember(userId, roomId))
            throw new CustomException(ErrorCode.NOT_CHAT_ROOM_MEMBER);
        
        nowChattingRooms.putIfAbsent(roomId, new HashSet<>());  //접속중인 클라이언트에 대한 웹소켓 삽입

        //특정 채팅방의 접속중 세션을 저장하는 set
        Set<WebSocketSession> nowChattingRoomSession = extractCurrentSession(roomId);

        //세션이 유효할 경우: 연결되어 있는 경우
        if (session.isOpen()) {
            //세션 삽입
            nowChattingRoomSession.add(session);
        } else {
            //연결이 끊긴 세션은 삽입x
            throw new CustomException(ErrorCode.INVALID_SESSION);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long roomId = extractRoomId(session);
        Set<WebSocketSession> nowChattingRoomSession = extractCurrentSession(roomId);
        //연결이 종료되면 접속 중인 세션을 저장하는 set에서 해당 세션 제거
        nowChattingRoomSession.remove(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        //메세지에서 payload get
        String payload = message.getPayload();
        //mapper를 이용해 dto로 변환
        ChatMessageRequestDto chatMessageRequestDto = objectMapper.readValue(payload, ChatMessageRequestDto.class);

        //사용자 정보
        Long loginId = extractLoginId(session);

        // 채팅방 현재 접속 중 멤버 확인: 메세지 보낼 대상
        Long roomId = extractRoomId(session);
        Set<WebSocketSession> nowChattingRoomSession = extractCurrentSession(roomId);

        //db에 메세지 저장
        chatUtil.saveMessage(chatMessageRequestDto, loginId, roomId);

        for (WebSocketSession webSocketSession : nowChattingRoomSession) {
            if (webSocketSession.isOpen()) {
                try {
                    webSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessageRequestDto)));
                } catch (IOException e) {
                    throw new CustomException(ErrorCode.INVALID_REQUEST);
                }
            }
        }
    }

    //uri 추출
    private URI extractUri(WebSocketSession session) {
        Optional<URI> optionalURI = Optional.ofNullable(session.getUri());
        if (optionalURI.isEmpty())
            throw new CustomException(ErrorCode.INVALID_URI);
        URI uri = optionalURI.get();

        return uri;
    }

    //roomId 추출
    private Long extractRoomId(WebSocketSession session) {
        URI uri = extractUri(session);
        String stringUri = uri.toString();

        Pattern pattern = Pattern.compile("(?<=/chats/send/)\\d+");
        Matcher matcher = pattern.matcher(stringUri);

        Long roomId = null;
        if (matcher.find()) {
            roomId = Long.valueOf(matcher.group());
            System.out.println(roomId);
            return roomId;
        } else {
            throw new CustomException(ErrorCode.NOT_FOUND_CHAT_ROOM);
        }
    }

    //특정 채팅방에 접속중인 웹소켓 세션 set 출력
    private Set<WebSocketSession> extractCurrentSession(Long roomId) {
        Set<WebSocketSession> nowChattingRoomSession = nowChattingRooms.get(roomId);

        return nowChattingRoomSession;
    }

    // 사용자 정보
    private Long extractLoginId(WebSocketSession session) {
        Principal principal = session.getPrincipal();
        if (principal == null)
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        Long loginId = Long.valueOf(principal.getName());

        return loginId;
    }
}
