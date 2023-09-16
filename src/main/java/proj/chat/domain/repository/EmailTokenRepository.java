package proj.chat.domain.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import proj.chat.domain.entity.EmailToken;
import proj.chat.domain.entity.Member;

@Transactional(readOnly = true)
public interface EmailTokenRepository extends JpaRepository<EmailToken, Long> {
    
    Optional<EmailToken> findByMemberId(Long memberId);
}
