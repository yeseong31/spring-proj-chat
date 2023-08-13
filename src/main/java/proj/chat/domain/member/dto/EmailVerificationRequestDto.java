package proj.chat.domain.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import proj.chat.domain.member.entity.EmailToken;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class EmailVerificationRequestDto {
    
    @Email(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")
    private String email;
    
    @NotEmpty(message = "인증 번호를 입력해주세요")
    private String token;
    
    @Builder
    public EmailVerificationRequestDto(String email, String token) {
        this.email = email;
        this.token = token;
    }
    
    public EmailToken dtoToEntity() {
        return EmailToken.builder()
                .token(token)
                .tokenTime(LocalDateTime.now())
                .expirationTime(LocalDateTime.now().plusMinutes(5))  // 토큰 유효시간: 5분
                .build();
    }
}
