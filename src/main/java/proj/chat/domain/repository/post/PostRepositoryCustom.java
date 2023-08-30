package proj.chat.domain.repository.post;

import java.util.List;
import proj.chat.domain.entity.Post;

public interface PostRepositoryCustom {
    
    List<Post> findSearch(String keyword);
}
