package proj.chat.domain.channel.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import proj.chat.domain.channel.entity.Channel;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ChannelResponseDto {
    
    private Long id;
    private String name;
    private int count;
    private int maxCount;
    private String uuid;
    private String createdBy;
    private LocalDateTime createdDate;
    
    public ChannelResponseDto(Channel entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.count = entity.getCount();
        this.maxCount = entity.getMaxCount();
        this.uuid = entity.getUuid();
        this.createdBy = entity.getCreatedBy();
        this.createdDate = entity.getCreatedDate();
    }
}
