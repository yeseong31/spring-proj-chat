package proj.chat.domain.member.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import proj.chat.domain.member.entity.Member;

@Transactional(readOnly = true)
public interface MemberRepository extends JpaRepository<Member, Long> {
    
    Optional<Member> findByEmail(String email);
    
    Optional<Member> findByUuid(String uuid);
    
    boolean existsByEmail(String email);
}
