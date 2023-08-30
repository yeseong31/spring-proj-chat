package proj.chat.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proj.chat.domain.entity.VoterPost;

public interface VoterPostRepository extends JpaRepository<VoterPost, Long> {

}
