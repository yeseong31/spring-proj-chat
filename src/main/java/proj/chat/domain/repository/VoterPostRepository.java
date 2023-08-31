package proj.chat.domain.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import proj.chat.domain.entity.Member;
import proj.chat.domain.entity.Post;
import proj.chat.domain.entity.VoterPost;

public interface VoterPostRepository extends JpaRepository<VoterPost, Long> {
    
    Optional<VoterPost> findByPostAndMember(Post post, Member member);
    
    boolean existsByPostAndMember(Post post, Member member);
}
