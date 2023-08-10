package proj.chat.dto.chat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import proj.chat.entity.Channel;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ChannelResponseDto {
    
    private Long id;
    private String name;
    private String password;
    private int maxCount;
    private String createdBy;
    
    public ChannelResponseDto(Channel entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.password = entity.getPassword();
        this.maxCount = entity.getMaxCount();
        this.createdBy = entity.getCreatedBy();
    }
}
