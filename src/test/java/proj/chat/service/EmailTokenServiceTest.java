package proj.chat.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import proj.chat.dto.email.EmailVerificationRequestDto;
import proj.chat.dto.email.EmailVerificationResponseDto;
import proj.chat.dto.member.MemberSaveRequestDto;
import proj.chat.security.EmailTokenService;

@Slf4j
@SpringBootTest
@Transactional
class EmailTokenServiceTest {
    
    @Autowired
    EmailTokenService emailTokenService;
    
    @Autowired
    MemberService memberService;
    
    @Test
    @DisplayName("이메일 인증 정보 저장")
    void save() {
        // given
        String email = "hong@test.com";
        
        MemberSaveRequestDto memberRequestDto = MemberSaveRequestDto.builder()
                .email(email)
                .name("홍길동")
                .password("Test123")
                .matchingPassword("Test123")
                .build();
        
        Long savedMemberId = memberService.save(memberRequestDto);
        
        EmailVerificationRequestDto emailRequestDto = EmailVerificationRequestDto.builder()
                .email(email)
                .token(UUID.randomUUID().toString().substring(0, 8))
                .build();
        
        emailTokenService.save(emailRequestDto);
        
        // when
        EmailVerificationResponseDto dto = emailTokenService.findByMemberId(savedMemberId);
        
        // then
        assertThat(dto.getEmail()).isEqualTo(emailRequestDto.getEmail());
    }
}