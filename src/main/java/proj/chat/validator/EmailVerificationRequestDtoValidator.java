package proj.chat.validator;

import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import proj.chat.dto.EmailVerificationRequestDto;
import proj.chat.entity.EmailToken;
import proj.chat.repository.EmailTokenRepository;
import proj.chat.repository.MemberRepository;

@Component
@RequiredArgsConstructor
public class EmailVerificationRequestDtoValidator implements Validator {
    
    private final MemberRepository memberRepository;
    private final EmailTokenRepository emailTokenRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(EmailVerificationRequestDto.class);
    }
    
    @Override
    public void validate(Object target, Errors errors) {
        EmailVerificationRequestDto dto = (EmailVerificationRequestDto) target;
        
        // 회원가입을 진행하지 않은 사용자인 경우
        if (!memberRepository.existsByEmail(dto.getEmail())) {
            errors.rejectValue("email", "invalid.email",
                    new Object[]{dto.getEmail()}, "회원가입을 먼저 진행해주세요");
        }
        
        // 인증 번호가 만들어지지 않은 사용자인 경우
        Optional<EmailToken> findEmailToken = emailTokenRepository.findByMemberEmail(dto.getEmail());
        EmailToken emailToken = null;
        
        if (findEmailToken.isEmpty()) {
            errors.rejectValue("email", "invalid.email",
                    new Object[]{dto.getEmail()}, "회원가입을 먼저 진행해주세요");
        } else {
            emailToken = findEmailToken.get();
        }
    
        // 토큰 값이 유효하지 않은 경우
        if (!Objects.requireNonNull(emailToken)
                .checkVerificationToken(dto.getToken(), passwordEncoder)) {
            
            errors.rejectValue("email", "invalid.token",
                    new Object[]{dto.getEmail()}, "유효하지 않은 토큰입니다");
        }
    
        // 토큰 유효 기간이 지난 경우
        // ...
    }
}
