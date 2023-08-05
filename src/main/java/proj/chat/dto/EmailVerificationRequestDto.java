package proj.chat.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import proj.chat.entity.EmailToken;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class EmailVerificationRequestDto {
    
    @NotEmpty(message = "이메일을 입력해주세요.")
    @Email(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")
    private String email;
    
    @NotEmpty(message = "인증 번호를 입력해주세요")
    private String verificationToken;
    
    @Builder
    public EmailVerificationRequestDto(String email, String verificationToken) {
        this.email = email;
        this.verificationToken = verificationToken;
    }
    
    public EmailToken dtoToEntity() {
        return EmailToken.builder()
                .verificationToken(verificationToken)
                .verificationTokenTime(LocalDateTime.now())
                .expirationTime(LocalDateTime.now().plusMinutes(5))  // 토큰 유효시간: 5분
                .build();
    }
}
