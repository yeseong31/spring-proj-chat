package proj.chat.controller;

import static proj.chat.dto.ChatMessageDto.MessageType.LEAVE;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import proj.chat.dto.ChatMessageDto;
import proj.chat.dto.ChatRoomDto;
import proj.chat.repository.ChatRoomRepository;

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
    private final ChatRoomRepository chatRoomRepository;
    
    /**
     * MessageMapping 애노테이션을 통해 websocket으로 들어오는 메시지를 송신 처리
     */
    @MessageMapping("/chat/enter")
    public void enterMember(
            @Payload ChatMessageDto chat, SimpMessageHeaderAccessor headerAccessor) {
        
        // 대상이 되는 채팅방
        ChatRoomDto targetChatRoomDto = chatRoomRepository.findByRoomId(chat.getRoomId());
        
        // 채팅방 사용자 증가 -> 사용자 UUID 반환
        String memberUUID = targetChatRoomDto.addMember(chat.getSender());
        
        // stomp session 조회
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes == null) {
            throw new IllegalStateException("세션을 찾을 수 없습니다");
        }
        
        // 사용자 UUID를 socket session에 저장
        headerAccessor.getSessionAttributes().put("memberUUID", memberUUID);
        headerAccessor.getSessionAttributes().put("roomId", chat.getRoomId());
        
        // 저장 후 "/sub/chat/room/roomId"로 메시지 전송
        chat.setMessage(chat.getSender() + "님이 입장했습니다");
        chat.setTime(LocalDateTime.now());
        template.convertAndSend("/sub/chat/room/" + targetChatRoomDto.getRoomId(), chat);
    }
    
    /**
     * 메시지 송신
     */
    @MessageMapping("/chat/message")
    public void message(@Payload ChatMessageDto chat) {
        
        log.info("[sendMessage] chat={}", chat);
        
        chat.setMessage(chat.getMessage());
        chat.setTime(LocalDateTime.now());
        template.convertAndSend("/sub/chat/room/" + chat.getRoomId(), chat);
    }
    
    /**
     * 채팅방 퇴장 시 EventListener를 통해 사용자 퇴장 확인
     */
    @EventListener
    public void webSocketDisconnectListener(SessionDisconnectEvent event) {
        
        log.info("[Disconnect] event={}", event);
        
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        
        // stomp session 조회
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes == null) {
            throw new IllegalStateException("세션을 찾을 수 없습니다");
        }
        
        // stomp 세션 내 사용자 UUID와 roomId 확인
        String memberUUID = (String) sessionAttributes.get("memberUUID");
        String roomId = (String) sessionAttributes.get("roomId");
        
        log.info("[Disconnect] headAccessor={}", headerAccessor);
        
        // 대상이 되는 채팅방
        ChatRoomDto targetChatRoomDto = chatRoomRepository.findByRoomId(roomId);
        
        // 채팅방에서 사용자 삭제
        targetChatRoomDto.deleteMember(memberUUID);
        
        // 사용자 퇴장 메시지 구성
        String memberName = targetChatRoomDto.getMemberName(memberUUID);
        if (memberName != null) {
            log.info("[Disconnect] 사용자 {} 퇴장", memberName);
            
            ChatMessageDto chat = ChatMessageDto.builder()
                    .type(LEAVE)
                    .sender(memberName)
                    .message(memberName + " 님이 나갔습니다")
                    .time(LocalDateTime.now())
                    .build();
            
            template.convertAndSend("/sub/chat/room/" + roomId, chat);
        }
    }
    
    /**
     * 채팅에 참여 중인 사용자 목록 반환
     */
    @GetMapping("/members")
    @ResponseBody
    public List<String> userList(String roomId) {
        return chatRoomRepository.getMemberList(roomId);
    }
}
