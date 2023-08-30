package proj.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proj.chat.domain.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

}
