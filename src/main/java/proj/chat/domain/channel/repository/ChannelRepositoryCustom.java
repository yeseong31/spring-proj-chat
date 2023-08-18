package proj.chat.domain.channel.repository;

import java.util.List;
import proj.chat.domain.channel.dto.ChannelMemberSearchDto;

public interface ChannelRepositoryCustom {
    
    List<ChannelMemberSearchDto> searchChannels(ChannelMemberSearchDto dto);
}
