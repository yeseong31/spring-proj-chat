package proj.chat.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import proj.chat.entity.EmailToken;
import proj.chat.entity.Member;

@Transactional(readOnly = true)
public interface EmailTokenRepository extends JpaRepository<EmailToken, Long> {
    
    boolean existsByMember(Member member);
    
    Optional<EmailToken> findByMemberId(Long memberId);
}
