package proj.chat.dto.chat;

import static lombok.AccessLevel.PROTECTED;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import proj.chat.entity.MessageType;

@Getter
@Setter
@NoArgsConstructor(access = PROTECTED)
public class PrevMessageDto {
    
    private MessageType type;    // 메시지 타입
    private String sender;       // 보내는 사람
    private String receiver;     // 받는 사람
    private String message;      // 메시지
    private LocalDateTime time;  // 채팅 발송 시간
    
    @Builder
    public PrevMessageDto(
            MessageType type, String sender, String receiver, String message, LocalDateTime time) {
        
        this.type = type;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.time = time;
    }
}

/*
[데이터 전송 형식(JSON)]

{
    "type": "TALK",
    "receiver": "ff076bb7-7e80-9bbb-49e7-24d507692bc7",
    "sender": "9bcc2479-a5ce-eb16-3c14-61c079fd170d",
    "message": "hello22"
}

*/
