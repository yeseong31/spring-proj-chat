package proj.chat.domain.dto.email;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import proj.chat.domain.entity.EmailToken;

@Getter
@Setter
@NoArgsConstructor
public class EmailVerificationResponseDto {
    
    private String email;
    
    public EmailVerificationResponseDto(EmailToken entity) {
        this.email = entity.getMember().getEmail();
    }
}
