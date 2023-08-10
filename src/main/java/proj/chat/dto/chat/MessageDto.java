package proj.chat.dto.chat;

import static lombok.AccessLevel.PROTECTED;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = PROTECTED)
public class MessageDto {
    
    public enum MessageType {
        ENTER, TALK, LEAVE
    }
    
    private MessageType type;    // 메시지 타입
    private String memberId;     // 사용자 ID
    private String channelId;    // 채널 ID
    private String message;      // 메시지
    private LocalDateTime time;  // 채팅 발송 시간
    
    @Builder
    public MessageDto(MessageType type, String memberId, String channelId, String message,
            LocalDateTime time) {
        
        this.type = type;
        this.memberId = memberId;
        this.channelId = channelId;
        this.message = message;
        this.time = time;
    }
}

/*

[데이터 전송 형식(JSON)]

{
"type": "ENTER",
"memberId": "test1234",
"channelId": "a4c2eb10-0a4d-4880-adb4-24704d0d61f2",
"message": "hi"
}

 */