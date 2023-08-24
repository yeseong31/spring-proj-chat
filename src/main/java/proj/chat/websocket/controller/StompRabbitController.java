package proj.chat.websocket.controller;

import static proj.chat.websocket.config.RabbitConfig.CHAT_QUEUE_NAME;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import proj.chat.websocket.dto.ChatDto;

@Slf4j
@Controller
@RequiredArgsConstructor
public class StompRabbitController {
    
    private final RabbitTemplate template;
    
    /**
     * 채팅방 입장 이벤트
     */
    @MessageMapping("chat.enter.{chatRoomId}")
    public void enter(ChatDto chat, @DestinationVariable String chatRoomId) {
        
        chat.setMessage("입장했습니다");
        chat.setRegDate(LocalDateTime.now());
        
        // exchange
        // template.convertAndSend(CHAT_EXCHANGE_NAME, "room." + chatRoomId, chat);
        
        // queue
        template.convertAndSend("room." + chatRoomId, chat);
        
        // topic
        template.convertAndSend("amq.topic", "room." + chatRoomId, chat);
    }
    
    /**
     * 메시지 전송
     */
    @MessageMapping("chat.message.{chatRoomId}")
    public void send(ChatDto chat, @DestinationVariable String chatRoomId) {
        
        chat.setRegDate(LocalDateTime.now());
        
        // exchange
        // template.convertAndSend(CHAT_EXCHANGE_NAME, "room." + chatRoomId, chat);
        
        // queue
        // template.convertAndSend("room." + chatRoomId, chat);
        
        // topic
        template.convertAndSend("amq.topic", "room." + chatRoomId, chat);
    }
    
    /**
     * receive()는 단순히 큐에 들어온 메시지를 소비한다. (디버그 용도)
     */
    @RabbitListener(queues = CHAT_QUEUE_NAME)
    public void received(ChatDto chat) {
        log.info("[receive] received={}", chat.getMessage());
    }
}
