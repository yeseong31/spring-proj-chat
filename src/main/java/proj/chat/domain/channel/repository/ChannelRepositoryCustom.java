package proj.chat.domain.channel.repository;

import java.util.List;
import proj.chat.domain.channel.dto.ChannelMemberSearchCond;
import proj.chat.domain.channel.entity.Channel;

public interface ChannelRepositoryCustom {
    
    List<Channel> findSearch(ChannelMemberSearchCond cond);
}
