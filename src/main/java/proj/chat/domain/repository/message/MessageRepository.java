package proj.chat.domain.repository.message;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import proj.chat.domain.entity.Message;

public interface MessageRepository extends MongoRepository<Message, String> {
    
    List<Message> findByChannelUuid(String channelUuid);
}
