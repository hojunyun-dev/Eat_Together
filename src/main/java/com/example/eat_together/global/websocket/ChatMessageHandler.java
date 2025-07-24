package com.example.eat_together.global.websocket;

import com.example.eat_together.domain.chat.dto.ChatMessageDto;
import com.example.eat_together.domain.chat.service.ChatService;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import com.example.eat_together.global.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Component
public class ChatMessageHandler extends TextWebSocketHandler {

    private final ChatService chatService;
    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;

    //Map<그룹: 멤버 set>
    static Map<Long, Set<WebSocketSession>> nowChattingRooms = new HashMap<>();

    //WebSocket 연결 시 동작
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("session: " + session);
        System.out.println("여기는 연결됐다는 뜻이고");

        Long roomId = extractRoomId(session);
        System.out.println("채팅방 번호: " + roomId);
        nowChattingRooms.putIfAbsent(roomId, new HashSet<>());

        Set<WebSocketSession> nowChattingRoomUsers = extractRoomMember(roomId);

        if(session.isOpen()) {
            nowChattingRoomUsers.add(session);
            System.out.println("=======================");
            for(WebSocketSession webSocketSession : nowChattingRoomUsers){System.out.println(webSocketSession);}
            System.out.println("=======================");
        }else{
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }
        session.sendMessage(new TextMessage("누군가 연결을 접속했습니다."));
    }

    //WebSocket 연결 종료 시 동작
    // WebSocket 연결 종료 직전 동작
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("여기는 연결 종료됐다는 뜻이야");
        Long roomId = extractRoomId(session);
        System.out.println("채팅방 번호: " + roomId);
        Set<WebSocketSession> nowChattingRoomUsers = extractRoomMember(roomId);
        nowChattingRoomUsers.remove(session);
    }

    //메세지 처리
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        System.out.println("메세지 핸들러~");
        //메세지에서 payload를 가져옵니다
        String payload = message.getPayload();
        //payload를 mapper를 이용해 dto로 변환합니다
        ChatMessageDto chatMessageDto = objectMapper.readValue(payload, ChatMessageDto.class);

        // 채팅방 현재 접속 중 멤버 확인
        Long roomId = extractRoomId(session);
        Set<WebSocketSession> nowChattingRoomUsers = extractRoomMember(roomId);
/*
        //사용자 id
        Long loginId = jwtUtil.getUserId(extractToken(session));
        System.out.println("사용자 id: " + loginId);
*/
        //메세지 저장
        chatService.saveMessage(chatMessageDto, roomId);

        for(WebSocketSession webSocketSession : nowChattingRoomUsers) {
            if(webSocketSession.isOpen()) {
                try {
                    System.out.println("유저: " + webSocketSession);
                    webSocketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessageDto)));
                }catch(IOException e){
                    System.out.println("유효하지 않은 세션이라 메세지 못 보냈대용");
                }
            }
        }
    }

    //uri 추출
    private URI extractUri(WebSocketSession session){
        Optional<URI> optionalURI = Optional.ofNullable(session.getUri());
        // 예외처리는 보다 구체적으로 이후 수정하겠습니다. 현재는 임의로 처리해뒀습니다.
        if(optionalURI.isEmpty())
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        URI uri = optionalURI.get();

        return uri;
    }

    // uri 스트링타입으로 추출
    private String extractStringUri(WebSocketSession session){
        URI uri = extractUri(session);
        String stringUri = uri.toString();

        return stringUri;
    }

    //roomId 추출
    private Long extractRoomId(WebSocketSession session){
        String stringUri = extractStringUri(session);
        System.out.println("1. StringUri = " + stringUri);
        Pattern pattern = Pattern.compile("(?<=/chats/)\\d+");
        System.out.println("2. Pattern = " + pattern);
        Matcher matcher = pattern.matcher(stringUri);
        System.out.println("3. Matcher = " + matcher);
        Long roomId = null;
        if(matcher.find()) {
            roomId = Long.valueOf(matcher.group());
            System.out.println(roomId);
            return roomId;
        }else{
            throw new CustomException(ErrorCode.NOT_FOUND_CHAT_ROOM);
        }
    }

    //특정 그룹의 멤버 set 추출
    private Set<WebSocketSession> extractRoomMember(Long roomId){
      Set<WebSocketSession> nowChattingRoomUsers = nowChattingRooms.get(roomId);
        return nowChattingRoomUsers;
    }

    // http 헤더의 토큰 추출
    private String extractToken(WebSocketSession session){
        List<String> httpHeader = session.getHandshakeHeaders().get("Authorization");
        String token = httpHeader.get(0).substring(7);

        return token;
    }

}
