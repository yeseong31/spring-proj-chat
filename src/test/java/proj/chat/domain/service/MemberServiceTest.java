package proj.chat.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import proj.chat.common.exception.DuplicatedMemberEmailException;
import proj.chat.common.exception.NotValidateEmailException;
import proj.chat.domain.dto.member.MemberSaveRequestDto;

@SpringBootTest
@Transactional
class MemberServiceTest {
    
    @Autowired
    MemberService memberService;
    
    static MemberSaveRequestDto createMemberSaveRequestDto() {
        return MemberSaveRequestDto.builder()
                .email("hong@test.com")
                .name("홍길동")
                .password("password1")
                .matchingPassword("password1")
                .build();
    }
    
    @Test
    @DisplayName("사용자 회원가입")
    void saveMember() {
        // given
        MemberSaveRequestDto requestDto = createMemberSaveRequestDto();
        
        // when
        Long savedId = memberService.save(requestDto);
        
        // then
        assertThat("hong@test.com").isEqualTo(requestDto.getEmail());
    }
    
    @Test
    @DisplayName("사용자 회원가입 - 중복 회원 예외")
    void duplicateMemberTest() {
        // given
        MemberSaveRequestDto requestDto = createMemberSaveRequestDto();
        
        Long savedId = memberService.save(requestDto);
        
        // when
        assertThatThrownBy(() -> memberService.save(requestDto))
                .isInstanceOf(DuplicatedMemberEmailException.class);
    }
    
    
    @Test
    @DisplayName("회원가입 - 이메일 @ 누락 예외")
    void validateEmailTest1() {
        // given
        MemberSaveRequestDto requestDto = MemberSaveRequestDto.builder()
                .email("hong")
                .name("홍길동")
                .password("password1")
                .matchingPassword("password1")
                .build();
        
        // when
        assertThatThrownBy(() -> memberService.save(requestDto))
                .isInstanceOf(NotValidateEmailException.class);
    }
    
    @Test
    @DisplayName("회원가입 - 이메일 local 누락 예외")
    void validateEmailTest2() {
        // given
        MemberSaveRequestDto requestDto = MemberSaveRequestDto.builder()
                .email("@test.com")
                .name("홍길동")
                .password("password1")
                .matchingPassword("password1")
                .build();
        
        // when
        assertThatThrownBy(() -> memberService.save(requestDto))
                .isInstanceOf(NotValidateEmailException.class);
    }
    
    @Test
    @DisplayName("회원가입 - 이메일 domain 누락 예외")
    void validateEmailTest3() {
        // given
        MemberSaveRequestDto requestDto = MemberSaveRequestDto.builder()
                .email("hong@test")
                .name("홍길동")
                .password("password1")
                .matchingPassword("password1")
                .build();
        
        // when
        assertThatThrownBy(() -> memberService.save(requestDto))
                .isInstanceOf(NotValidateEmailException.class);
    }
    
    @Test
    @DisplayName("회원가입 - 이메일 domain 누락 예외2")
    void validateEmailTest4() {
        // given
        MemberSaveRequestDto requestDto = MemberSaveRequestDto.builder()
                .email("hong@test.")
                .name("홍길동")
                .password("password1")
                .matchingPassword("password1")
                .build();
        
        // when
        assertThatThrownBy(() -> memberService.save(requestDto))
                .isInstanceOf(NotValidateEmailException.class);
    }
}