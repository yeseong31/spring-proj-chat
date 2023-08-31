package proj.chat.domain.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import proj.chat.domain.entity.Comment;
import proj.chat.domain.entity.Member;
import proj.chat.domain.entity.VoterComment;

public interface VoterCommentRepository extends JpaRepository<VoterComment, Long> {
    
    Optional<VoterComment> findByCommentAndMember(Comment comment, Member member);
    
    boolean existsByCommentAndMember(Comment comment, Member member);
}
