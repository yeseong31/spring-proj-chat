package proj.chat.domain.message.dto;

import static lombok.AccessLevel.PROTECTED;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import proj.chat.domain.message.entity.Message;

@Getter
@Setter
@ToString
@NoArgsConstructor(access = PROTECTED)
public class MessageDto {
    
    private String channelUuid;  // 채널 UUID
    private String memberName;   // 메시지 송신자 이름
    private String content;      // 메시지
    
    @Builder
    public MessageDto(Message entity) {
        this.channelUuid = entity.getChannel().getUuid();
        this.memberName = entity.getMember().getName();
        this.content = entity.getContent();
    }
    
    public Message dtoToEntity() {
        return Message.builder()
                .content(content)
                .build();
    }
}

/*

[데이터 전송 형식(JSON)]

{
"memberName": "test1234",
"channelId": "a4c2eb10-0a4d-4880-adb4-24704d0d61f2",
"content": "hi"
}

 */