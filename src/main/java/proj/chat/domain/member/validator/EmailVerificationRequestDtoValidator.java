package proj.chat.domain.member.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import proj.chat.domain.member.dto.EmailVerificationRequestDto;

@Component
@RequiredArgsConstructor
public class EmailVerificationRequestDtoValidator implements Validator {
    
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(EmailVerificationRequestDto.class);
    }
    
    @Override
    public void validate(Object target, Errors errors) {
        EmailVerificationRequestDto dto = (EmailVerificationRequestDto) target;
        
        // TODO: 토큰 유효 기간이 지난 경우
        
    }
}