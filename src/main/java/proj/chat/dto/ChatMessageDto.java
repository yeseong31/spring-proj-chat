package proj.chat.dto;

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
}
