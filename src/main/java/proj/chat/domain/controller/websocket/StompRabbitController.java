package proj.chat.domain.controller.websocket;

import static proj.chat.websocket.config.RabbitConfig.CHAT_QUEUE_NAME;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import proj.chat.domain.dto.message.MessageDto;
import proj.chat.domain.service.MessageService;

@Slf4j
@Controller
@RequiredArgsConstructor
public class StompRabbitController {
    
    private final RabbitTemplate template;
    private final MessageService messageService;
    
    /**
     * 채팅방 입장 이벤트
     */
    @MessageMapping("chat.enter.{channelUuid}")
    public void enter(MessageDto messageDto, @DestinationVariable String channelUuid) {
        
        messageDto.setMessage(messageDto.getMemberName() + "님이 입장했습니다");
        messageDto.setCreatedDate(LocalDateTime.now());
    
        messageService.save(messageDto);
        log.info(String.format("[enter] 채팅방 입장: UUID=%s", channelUuid));
        
        // topic
        template.convertAndSend("amq.topic", "room." + channelUuid, messageDto);
    }
    
    /**
     * 메시지 전송
     */
    @MessageMapping("chat.message.{channelUuid}")
    public void send(MessageDto messageDto, @DestinationVariable String channelUuid) {
        
        messageDto.setCreatedDate(LocalDateTime.now());
    
        messageService.save(messageDto);
        log.info(String.format(
                        "[send] 채팅: UUID=%s, 메시지=%s", channelUuid, messageDto.getMessage()));
        
        // topic
        template.convertAndSend("amq.topic", "room." + channelUuid, messageDto);
    }
    
    /**
     * receive()는 단순히 큐에 들어온 메시지를 소비한다. (디버그 용도)
     */
    @RabbitListener(queues = CHAT_QUEUE_NAME)
    public void received(MessageDto messageDto) {
        log.info("[receive] received={}", messageDto.getMessage());
    }
}
