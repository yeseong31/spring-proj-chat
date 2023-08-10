package proj.chat.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import proj.chat.entity.Channel;

@Transactional(readOnly = true)
public interface ChannelRepository extends JpaRepository<Channel, Long> {
    
    Optional<Channel> findByUuid(String uuid);
}