package proj.chat.domain.channel.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChannelMemberSearchDto {
    
    private String memberName;
    private String channelName;
    
    @Builder
    @QueryProjection
    public ChannelMemberSearchDto(String memberName, String channelName) {
        this.memberName = memberName;
        this.channelName = channelName;
    }
}
