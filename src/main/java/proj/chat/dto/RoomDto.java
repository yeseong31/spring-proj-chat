package proj.chat.dto;

import static lombok.AccessLevel.PROTECTED;
import static proj.chat.dto.ChatMessageDto.MessageType.ENTER;
import static proj.chat.dto.ChatMessageDto.MessageType.LEAVE;
import static proj.chat.dto.ChatMessageDto.MessageType.TALK;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.socket.WebSocketSession;
import proj.chat.service.RoomService;

@Getter
@NoArgsConstructor(access = PROTECTED)
public class RoomDto {
    
    private String roomId;    // 채팅방 ID
    private String roomName;  // 채팅방 이름
    
    private final Set<WebSocketSession> sessions = new HashSet<>();
    
    @Builder
    public RoomDto(String roomName) {
        this.roomId = UUID.randomUUID().toString().substring(0, 8);
        this.roomName = roomName;
    }
    
    // JSON 데이터를 받아 WebSocketChatHandler에서 해당 데이터에 담긴 roomId를 chatService에서 조회
    // 해당 roomId의 방을 찾아서 JSON 데이터에 담긴 메시지를 해당 채팅방으로 전송
    public void handlerActions(
            WebSocketSession session, ChatMessageDto message, RoomService chatService) {
        
        if (message.getType().equals(ENTER)) {
            // '입장' 이벤트인 경우
            sessions.add(session);
            message.setMessage(message.getSender() + "님이 들어왔습니다");
            sendMessage(message, chatService);
        } else if (message.getType().equals(TALK)) {
            // '대화' 이벤트인 경우
            message.setMessage(message.getMessage());
            sendMessage(message, chatService);
        } else if (message.getType().equals(LEAVE)) {
            // '퇴장' 이벤트인 경우
            sessions.remove(session);
            message.setMessage(message.getSender() + "님이 나갔습니다");
            sendMessage(message, chatService);
        }
    }
    
    private <T> void sendMessage(T message, RoomService chatService) {
        sessions.parallelStream()
                .forEach(sessions -> chatService.sendMessage(sessions, message));
    }
}