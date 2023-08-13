package proj.chat.security.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import proj.chat.entity.EmailToken;

@Getter
@Setter
@NoArgsConstructor
public class EmailVerificationResponseDto {
    
    private String email;
    
    public EmailVerificationResponseDto(EmailToken entity) {
        this.email = entity.getMember().getEmail();
    }
}
