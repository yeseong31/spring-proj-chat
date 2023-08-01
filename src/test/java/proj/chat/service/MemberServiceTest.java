package proj.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import proj.chat.dto.MemberResponseDto;
import proj.chat.dto.MemberSaveRequestDto;
import proj.chat.exception.DuplicatedMemberEmailException;
import proj.chat.exception.NotValidateEmailException;

@Slf4j
@Transactional
@SpringBootTest
class MemberServiceTest {
    
    @Autowired
    MemberService memberService;
    
    @Autowired
    PasswordEncoder passwordEncoder;
    
    @Test
    @DisplayName("회원가입")
    void saveMember() {
        // given
        MemberSaveRequestDto requestDto = MemberSaveRequestDto.builder()
                .email("hong@test.com")
                .name("홍길동")
                .password("Test123")
                .matchingPassword("Test123")
                .build();
        
        Long savedId = memberService.save(requestDto);
        
        // when
        MemberResponseDto responseDto = memberService.findById(savedId);
        
        // then
        assertThat(responseDto.getEmail()).isEqualTo("hong@test.com");
    }
}