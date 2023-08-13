package proj.chat.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import proj.chat.domain.member.dto.MemberSaveRequestDto;
import proj.chat.domain.member.repository.MemberRepository;

@Component
@RequiredArgsConstructor
public class MemberSaveRequestDtoValidator implements Validator {
    
    private final MemberRepository memberRepository;
    
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(MemberSaveRequestDto.class);
    }
    
    @Override
    public void validate(Object target, Errors errors) {
        // 중복된 이메일인 경우
        MemberSaveRequestDto dto = (MemberSaveRequestDto) target;
        
        if (memberRepository.existsByEmail(dto.getEmail())) {
            errors.rejectValue("email", "invalid.email",
                    new Object[]{dto.getEmail()}, "이미 사용 중인 이메일입니다");
        }
        
        // 이메일 형식이 잘못된 경우
        String regex = "[a-z0-9]+@[a-z]+\\.[a-z]{2,3}";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(dto.getEmail());
        
        if (!m.matches()) {
            errors.rejectValue("email", "invalid.email",
                    new Object[]{dto.getEmail()}, "잘못된 형식의 이메일입니다");
        }
        
        // 비밀번호가 일치하지 않는 경우
        if (!dto.getPassword().equals(dto.getMatchingPassword())) {
            errors.rejectValue("matchingPassword", "password.incorrect",
                    new Object[]{dto.getPassword()}, "비밀번호가 일치하지 않습니다");
        }
    }
}
