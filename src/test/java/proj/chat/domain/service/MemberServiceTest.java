package proj.chat.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
    void duplicateMember() {
        // given
        MemberSaveRequestDto requestDto = createMemberSaveRequestDto();
        
        Long savedId = memberService.save(requestDto);
        
        // when
        assertThatThrownBy(() -> memberService.save(requestDto))
                .isInstanceOf(DuplicatedMemberEmailException.class);
    }
    
    
    @ParameterizedTest(name = "#{index} 이메일 '{0}' 형식 확인")
    @ValueSource(strings = {"hong", "@test.com", "hong@test.", "hong@test."})
    @DisplayName("회원가입 - 이메일 형식 누락")
    void validateEmail(String email) {
        // given
        MemberSaveRequestDto requestDto = MemberSaveRequestDto.builder()
                .email(email)
                .name("홍길동")
                .password("password1")
                .matchingPassword("password1")
                .build();
        
        // when
        assertThatThrownBy(() -> memberService.save(requestDto))
                .isInstanceOf(NotValidateEmailException.class);
    }
}