package proj.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proj.chat.domain.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

}
