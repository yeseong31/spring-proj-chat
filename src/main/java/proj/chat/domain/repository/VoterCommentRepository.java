package proj.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proj.chat.domain.entity.VoterComment;

public interface VoterCommentRepository extends JpaRepository<VoterComment, Long> {

}
