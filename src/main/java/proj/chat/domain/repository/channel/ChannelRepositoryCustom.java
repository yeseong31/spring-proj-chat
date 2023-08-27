package proj.chat.domain.repository.channel;

import java.util.List;
import proj.chat.domain.entity.Channel;

public interface ChannelRepositoryCustom {
    
    List<Channel> findSearch(String keyword);
}
