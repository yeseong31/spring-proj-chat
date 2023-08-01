package proj.chat.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import proj.chat.entity.Member;

@SpringBootTest
@Transactional(readOnly = true)
class MemberRepositoryTest {
    
    @Autowired
    MemberRepository memberRepository;
    
    @Test
    @DisplayName("사용자 저장")
    void saveMember() {
        // given
        Member member = Member.builder()
                .name("홍길동")
                .email("hong@test.com")
                .password("Test123")
                .build();
        
        // when
        Member savedMember = memberRepository.save(member);
        
        // then
        assertThat(savedMember).isEqualTo(member);
    }
    
    @Test
    @DisplayName("사용자 전체 조회")
    void findAllMembers() {
        // given
        Member member1 = Member.builder()
                .name("홍길동")
                .email("hong@test.com")
                .password("Test123")
                .build();
    
        Member member2 = Member.builder()
                .name("홍길동")
                .email("hong@test.com")
                .password("Test123")
                .build();
    
        memberRepository.save(member1);
        memberRepository.save(member2);
        
        // when
        List<Member> findMembers = memberRepository.findAll();
    
        // then
        assertThat(findMembers.size()).isEqualTo(2);
    }
    
    @Test
    @DisplayName("사용자 이메일로 조회")
    void findByEmail() {
        // given
        Member member = Member.builder()
                .name("홍길동")
                .email("hong@test.com")
                .password("Test123")
                .build();
    
        memberRepository.save(member);
        
        // when
        Member findMember = memberRepository.findByEmail("hong@test.com")
                .orElseThrow(NoSuchElementException::new);
    
        // then
        assertThat(findMember).isEqualTo(member);
    }
}