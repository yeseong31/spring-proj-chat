package proj.chat.domain.repository.channel;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import proj.chat.domain.entity.Channel;

@Transactional(readOnly = true)
public interface ChannelRepository extends JpaRepository<Channel, Long>, ChannelRepositoryCustom {
    
    Optional<Channel> findByUuid(String uuid);
    
    boolean existsByUuid(String uuid);
}
