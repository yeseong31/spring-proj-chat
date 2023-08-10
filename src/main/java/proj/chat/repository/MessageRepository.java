package proj.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proj.chat.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

}
