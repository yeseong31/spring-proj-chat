package proj.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proj.chat.domain.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
