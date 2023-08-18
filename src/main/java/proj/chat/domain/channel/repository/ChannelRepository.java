package proj.chat.domain.channel.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import proj.chat.domain.channel.entity.Channel;

@Transactional(readOnly = true)
public interface ChannelRepository extends JpaRepository<Channel, Long> {
    
    Optional<Channel> findByUuid(String uuid);
    
    boolean existsByUuid(String uuid);
}
