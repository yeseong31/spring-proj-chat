package proj.chat.websocket.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import proj.chat.domain.message.dto.MessageDto;

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
     * MessageMapping 애노테이션을 통해 WebSocket으로 들어오는 메시지를 송신 처리
     */
    @MessageMapping("/enter")
    public void enter(@Payload MessageDto message) {
        /*
        {
            "memberName": "test1234",
            "channelUuid": "a4c2eb10-0a4d-4880-adb4-24704d0d61f2"
        }
         */
        
        // 입장 메시지 구성
        message.setContent(message.getMemberName() + "님이 입장했습니다");
        
        log.info("[ENTER] {}님이 입장했습니다", message.getMemberName());
        
        // 메시지 전달
        template.convertAndSend(
                "/sub/channel/" + message.getChannelUuid(),
                message);
    }
    
    /**
     * 메시지 송신
     */
    @MessageMapping("/message")
    public void message(@Payload MessageDto message) {
        /*
        {
            "memberName": "test1234",
            "channelUuid": "a4c2eb10-0a4d-4880-adb4-24704d0d61f2",
            "content": "hi"
        }
         */
        
        log.info("[MESSAGE] {}", message.getContent());
        
        // 메시지 전달
        template.convertAndSend(
                "/sub/channel/" + message.getChannelUuid(),
                message);
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
