package proj.chat.domain.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import proj.chat.domain.member.entity.EmailToken;

@Getter
@Setter
@NoArgsConstructor
public class EmailVerificationResponseDto {
    
    private String email;
    private String token;
    
    public EmailVerificationResponseDto(EmailToken entity) {
        this.email = entity.getMember().getEmail();
        this.token = entity.getToken();
    }
}
