package proj.chat.domain.controller.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import proj.chat.domain.dto.message.MessageDto;

/**
 * 채팅을 송수신(pub/sub)하는 컨트롤러
 * <p>
 * MessageMapping 애노테이션: 클라이언트로부터 요청 메시지를 받으면 @SendTo로 설정한 구독 클라이언트들에게 메시지 송신
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class StompChatController {
    
    private final SimpMessageSendingOperations template;  // 특정 브로커로 메시지 전달
    
    /**
     * 채팅방 입장 시 EventListener를 통해 사용자 입장 확인
     */
    @EventListener
    public void webSocketConnectListener(SessionConnectEvent event) {
        
        log.info("[Connect] event={}", event);
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        log.info("[Connect] headAccessor={}", headerAccessor);
    }
    
    /**
     * 메시지 송신
     */
    @MessageMapping("/message")
    public void message(@Payload MessageDto message) {
        
        log.info("[{}] {}: {}",
                message.getChannelUuid().substring(0, 8),
                message.getMemberUuid().substring(0, 8),
                message.getMessage());
        
        // 메시지 전달
        template.convertAndSend(
                String.format("/sub/channel/%s", message.getChannelUuid()), message);
    }
    
    /**
     * 채팅방 퇴장 시 EventListener를 통해 사용자 퇴장 확인
     */
    @EventListener
    public void webSocketDisconnectListener(SessionDisconnectEvent event) {
        
        log.info("[Disconnect] event={}", event);
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        log.info("[Disconnect] headAccessor={}", headerAccessor);
    }
}
