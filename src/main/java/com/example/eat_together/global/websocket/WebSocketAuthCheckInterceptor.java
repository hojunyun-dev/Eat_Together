package com.example.eat_together.global.websocket;

import com.example.eat_together.domain.chat.entity.ChatRoomUser;
import com.example.eat_together.domain.chat.service.ChatUtil;
import com.example.eat_together.global.exception.CustomException;
import com.example.eat_together.global.exception.ErrorCode;
import com.example.eat_together.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.Comment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.eat_together.global.util.JwtAuthenticationFilter.AUTHORIZATION_HEADER;
import static com.example.eat_together.global.util.JwtAuthenticationFilter.BEARER_PREFIX;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthCheckInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final ChatUtil chatUtil;


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        if (accessor.getCommand() == StompCommand.CONNECT) {
            //변경 전: 토큰 없으면 예외로 연결 실패
            //변경 후: 토큰이 있으면 principal 세팅, 없으면 "그대로 통과"
            //이유: 채팅 클라이언트가 CONNECT에 Authorization을 안 보낼 수도 있어서 연결은 막지 않음
            try {
                Principal principal = getPrincipal(accessor); // 내부적으로 토큰 검증(없거나 잘못되면 예외)
                accessor.getSessionAttributes().put("user", principal);
                accessor.setUser(principal);
                log.debug("[WS] CONNECT principal={}", principal.getName());
            } catch (CustomException ignore) {
                log.debug("[WS] CONNECT without token -> allowed (chat compatibility)");
            }
            accessor.setLeaveMutable(true);
            return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
        }

        if (accessor.getCommand() == StompCommand.SUBSCRIBE) {
            //새로운 알림 채널만 본인 확인 강제: /sub/notifications/{userId}
            String destination = accessor.getDestination();
            if (destination != null && destination.startsWith("/sub/notifications/")) {
                String pathUserId = destination.substring(destination.lastIndexOf('/') + 1);

                //새로운 알림 채널 구독 시에는 반드시 인증 필요
                //CONNECT에서 principal이 없었더라도 여기서 재인증 시도(토큰 없거나 잘못되면 예외)
                Principal principal = accessor.getUser();
                if (principal == null) {
                    principal = getPrincipal(accessor); //토큰 필수(예외시 구독 차단)
                    accessor.setUser(principal);
                }

                String principalUserId = principal.getName(); //JwtUtil이 userId를 name으로 세팅한다고 가정
                if (!Objects.equals(pathUserId, principalUserId)) {
                    //다른 사용자 알림 채널 구독 시도 차단
                    throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
                }

            }

            // 멤버 여부 확인
            if(destination != null && destination.startsWith("/sub/roomId/")){
                Principal principal = accessor.getUser();
                if (principal == null) {
                    principal = (Principal) accessor.getSessionAttributes().get("user");
                }
                Long userId = Long.valueOf(principal.getName());
                Long roomId = extractRoomId(accessor);

                try {
                    ChatRoomUser chatRoomUser = chatUtil.getGroupMember(userId, roomId);

                    System.out.println("chatRoomUser = " + chatRoomUser.getUser().getUserId());
                    if (chatRoomUser == null || !userId.equals(chatRoomUser.getUser().getUserId())) {
                        System.out.println("========================1======================");
                        log.debug("NOT CHATROOM USER");
                        throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
                    }
                } catch (Exception e) {
                    System.out.println("========================5======================");
                    throw  new CustomException(ErrorCode.FORBIDDEN_ACCESS);
                }

                accessor.setLeaveMutable(true);
                return MessageBuilder.createMessage(message.getPayload(), accessor.getMessageHeaders());
            }
        }

        return message;
    }

    protected Principal getPrincipal(StompHeaderAccessor accessor) {
        String token = resolveToken(accessor);
        Principal principal = jwtUtil.getAuthentication(token);

        return principal;
    }

    private String resolveToken(StompHeaderAccessor accessor) {
        String authorization = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null) {
            System.out.println("========================2======================");
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }
        if (!authorization.startsWith(BEARER_PREFIX)) {
            System.out.println("========================3======================");
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }
        return authorization.substring(BEARER_PREFIX.length());
    }

    //roomId 추출
    private Long extractRoomId(StompHeaderAccessor accessor){
            String stringUri = accessor.getDestination();
            if(stringUri == null) {
                System.out.println("========================4======================");
                throw new CustomException(ErrorCode.INVALID_REQUEST);
            }
            System.out.println("1. StringUri = " + stringUri);
            Pattern pattern = Pattern.compile("(?<=/sub/roomId/)\\d+");
            System.out.println("2. Pattern = " + pattern);
            Matcher matcher = pattern.matcher(stringUri);
            System.out.println("3. Matcher = " + matcher);
            Long roomId;
            try {
                if (matcher.find()) {
                    roomId = Long.valueOf(matcher.group());
                    return roomId;
                } else {
                    throw new CustomException(ErrorCode.NOT_FOUND_CHAT_ROOM);
                }
            } catch (NumberFormatException e) {
                throw new CustomException(ErrorCode.INVALID_REQUEST);
            }
    }


}
