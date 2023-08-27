package proj.chat.domain.dto.member;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import proj.chat.domain.entity.FromSocial;
import proj.chat.domain.entity.Member;
import proj.chat.domain.entity.MemberRole;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class MemberSaveRequestDto {

    @NotBlank(message = "이름을 입력해주세요")
    @Length(min = 3, max = 20, message = "이름은 길이가 3~20 사이여야 합니다")
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{3,20}$",
            message = "엉어, 숫자, 한글, 언더바(_), 대시(-)만 입력할 수 있습니다")
    private String name;
    
    @NotEmpty(message = "이메일을 입력해주세요")
    @Email(regexp = "[a-z0-9]+@[a-z]+\\.[a-z]{2,3}")
    private String email;
    
    @Length(min = 4, max = 16, message = "비밀번호는 4~16자여야 합니다")
    @NotEmpty(message = "비밀번호는 필수 항목입니다")
    private String password;
    
    @NotEmpty(message = "비밀번호 확인은 필수 항목입니다")
    private String matchingPassword;
    
    private FromSocial fromSocial;
    
    private MemberRole role;
    
    public Member dtoToEntity() {
        return Member.builder()
                .name(name)
                .email(email)
                .password(password)
                .fromSocial(fromSocial)
                .role(role)
                .build();
    }
    
    @Builder
    public MemberSaveRequestDto(String name, String email, String password,
            String matchingPassword, FromSocial fromSocial, MemberRole role) {
        
        this.name = name;
        this.email = email;
        this.password = password;
        this.matchingPassword = matchingPassword;
        this.fromSocial = fromSocial;
        this.role = role;
    }
}
