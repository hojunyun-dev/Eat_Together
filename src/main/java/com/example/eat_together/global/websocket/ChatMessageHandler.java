package com.example.eat_together.global.websocket;

import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.net.URI;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Component
public class ChatMessageHandler extends TextWebSocketHandler {
    //Map<그룹: 멤버 set>
    Map<Long, Set<WebSocketSession>> nowChattingRooms = new HashMap<>();

/*    //Set<멤버>
    Set<WebSocketSession> nowChattingRoomUsers = new HashSet<>();


 */
    //WebSocket 연결 시 동작
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long roomId = extractRoomId(session);
        Set<WebSocketSession> nowChattingRoomUsers = extractRoomMember(roomId);
        nowChattingRoomUsers.add(session);
        nowChattingRooms.put(roomId, nowChattingRoomUsers);
    }

    //WebSocket 연결 종료 시 동작
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long roomId = extractRoomId(session);
        Set<WebSocketSession> nowChattingRoomUsers = extractRoomMember(roomId);
        nowChattingRoomUsers.remove(session);
   //     session.sendMessage(new TextMessage("연결 종료"));
    }

    //메세지 처리
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String content = message.getPayload();

        Long roomId = extractRoomId(session);
        Set<WebSocketSession> nowChattingRoomUsers = extractRoomMember(roomId);

        for(WebSocketSession webSocketSession : nowChattingRoomUsers)
            webSocketSession.sendMessage(new TextMessage(content));
    }

    //uri 추출
    private String extractUri(WebSocketSession session){
        Optional<URI> optionalURI = Optional.ofNullable(session.getUri());
        // 예외처리는 보다 구체적으로 이후 수정하겠습니다. 현재는 임의로 처리해뒀습니다.
        if(optionalURI.isEmpty())
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        String stringUri = optionalURI.get().toString();
        return stringUri;
    }

    //roomId 추출
    private Long extractRoomId(WebSocketSession session){
        String stringUri = extractUri(session);
        Pattern pattern = Pattern.compile("(?<=/chats/)\\d+");
        Matcher matcher = pattern.matcher(stringUri);
        Long roomId = Long.valueOf(matcher.group());
        return roomId;
    }

    //특정 그룹의 멤버 set 추출
    private Set<WebSocketSession> extractRoomMember(Long roomId){
        Set<WebSocketSession> nowChattingRoomUsers = nowChattingRooms.get(roomId);
        return nowChattingRoomUsers;
    }
}
