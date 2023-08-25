package proj.chat.repository.channel;

import java.util.List;
import proj.chat.entity.Channel;

public interface ChannelRepositoryCustom {
    
    List<Channel> findSearch(String keyword);
}
