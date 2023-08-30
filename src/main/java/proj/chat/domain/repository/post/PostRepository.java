package proj.chat.domain.repository.post;

import org.springframework.data.jpa.repository.JpaRepository;
import proj.chat.domain.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

}
