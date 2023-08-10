package proj.chat.dto.chat;

import static lombok.AccessLevel.PROTECTED;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import proj.chat.entity.Message;
import proj.chat.entity.MessageType;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = PROTECTED)
public class MessageDto {
    
    private MessageType type;    // 메시지 타입
    private String memberId;     // 사용자 ID
    private String channelId;    // 채널 ID
    private String content;      // 메시지
    private LocalDateTime time;  // 채팅 발송 시간
    
    @Builder
    public MessageDto(Message entity) {
        this.type = entity.getType();
        this.memberId = entity.getMember().getName();
        this.channelId = entity.getChannel().getUuid();
        this.content = entity.getContent();
        this.time = entity.getCreatedDate();
    }
    
    public Message dtoToEntity() {
        return Message.builder()
                .type(type)
                .content(content)
                .build();
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