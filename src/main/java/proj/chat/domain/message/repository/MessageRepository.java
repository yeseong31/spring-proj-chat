package proj.chat.domain.message.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proj.chat.domain.message.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

}
