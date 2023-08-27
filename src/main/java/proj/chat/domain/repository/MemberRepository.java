package proj.chat.domain.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import proj.chat.domain.entity.Member;

@Transactional(readOnly = true)
public interface MemberRepository extends JpaRepository<Member, Long> {
    
    Optional<Member> findByEmail(String email);
    
    Optional<Member> findByUuid(String uuid);
    
    Optional<Member> findByName(String name);
    
    boolean existsByEmail(String email);
}
