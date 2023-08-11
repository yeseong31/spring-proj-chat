package proj.chat.dto.chat;

import java.time.LocalDateTime;
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
    private int count;
    private int maxCount;
    private String createdBy;
    private LocalDateTime createdDate;
    
    public ChannelResponseDto(Channel entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.count = entity.getCount();
        this.maxCount = entity.getMaxCount();
        this.createdBy = entity.getCreatedBy();
        this.createdDate = entity.getCreatedDate();
    }
}
