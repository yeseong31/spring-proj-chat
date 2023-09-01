package proj.chat.domain.entity;

import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Document(collation = "message")
public class Message extends BaseTimeEntity {
    
    @Id
    private String id;
    
    @Field("channel_uuid")
    public String channelUuid;
    
    @Field("member_uuid")
    private String memberUuid;
    
    @Field("member_name")
    private String memberName;
    
    @Field("content")
    private String content;
    
    
    @Builder
    public Message(String id, String channelUuid, String memberUuid, String memberName, String content) {
        this.id = id;
        this.channelUuid = channelUuid;
        this.memberUuid = memberUuid;
        this.memberName = memberName;
        this.content = content;
    }
    
    @Override
    public String toString() {
        return String.format(
                "Message[id=%s, channelUuid=%s, memberUuid=%s]",
                id, channelUuid, memberUuid);
    }
    
    public Message update(String content) {
        this.content = content;
        return this;
    }
}
