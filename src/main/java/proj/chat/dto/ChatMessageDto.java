package proj.chat.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChatMessageDto {
    
    public enum MessageType {
        ENTER, TALK, LEAVE
    }
    
    private MessageType type;    // 메시지 타입
    private String roomId;       // 방 ID
    private String sender;       // 채팅 송신자
    private String message;      // 메시지
    private LocalDateTime time;  // 채팅 발송 시간
    
    @Builder
    public ChatMessageDto(
            MessageType type, String roomId, String sender, String message, LocalDateTime time) {
        
        this.type = type;
        this.roomId = roomId;
        this.sender = sender;
        this.message = message;
        this.time = time;
    }
}
