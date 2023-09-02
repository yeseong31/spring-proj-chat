package proj.chat.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import proj.chat.common.exception.DataNotFoundException;
import proj.chat.domain.entity.Member;

@SpringBootTest
@Transactional
class MemberRepositoryTest {
    
    @Autowired
    MemberRepository memberRepository;
    
    static Member createMember(String email) {
        return Member.builder()
                .email(email)
                .name("홍길동")
                .uuid(UUID.randomUUID().toString())
                .password("Test123")
                .build();
    }
    
    @Test
    @DisplayName("사용자 회원가입")
    void saveMember() {
        // given
        Member member = createMember("hong@test.com");
        
        // when
        Member savedMember = memberRepository.save(member);
        
        // then
        assertThat(savedMember).isEqualTo(member);
    }
    
    @Test
    @DisplayName("사용자 전체 조회")
    void findAllMembers() {
        // given
        Member member1 = createMember("hong1@test.com");
        Member member2 = createMember("hong2@test.com");
        
        memberRepository.save(member1);
        memberRepository.save(member2);
        
        // when
        List<Member> findMembers = memberRepository.findAll();
        
        // then
        assertThat(2).isEqualTo(findMembers.size());
    }
    
    @Test
    @DisplayName("사용자 이메일로 조회")
    void findByEmail() {
        // given
        Member member = createMember("hong@test.com");
        
        memberRepository.save(member);
        
        // when
        Member findMember = memberRepository.findByEmail("hong@test.com")
                .orElseThrow(() -> new DataNotFoundException("사용자 조회 오류"));
        
        // then
        assertThat(findMember).isEqualTo(member);
    }
    
    @Test
    @DisplayName("사용자 UUID로 조회")
    void findByUuid() {
        // given
        Member member = createMember("hong@test.com");
    
        Member savedMember = memberRepository.save(member);
    
        // when
        Member findMember = memberRepository.findByUuid(savedMember.getUuid())
                .orElseThrow(() -> new DataNotFoundException("사용자 조회 오류"));
        
        // then
        assertThat(findMember).isEqualTo(member);
    }
}