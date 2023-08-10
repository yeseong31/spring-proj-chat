package proj.chat.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class LoginRequestDto {

    @NotEmpty(message = "이메일을 입력해주세요.")
    @Email(regexp = "^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")
    private String email;
    
    @NotEmpty(message = "비밀번호를 입력해주세요.")
    @Length(min = 4, max = 16, message = "비밀번호는 4~16자여야 합니다.")
    private String password;
    
    @Builder
    public LoginRequestDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
