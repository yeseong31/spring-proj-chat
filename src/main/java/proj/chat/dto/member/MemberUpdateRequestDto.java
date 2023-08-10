package proj.chat.dto.member;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import proj.chat.entity.Member;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class MemberUpdateRequestDto {
    
    private Long id;
    
    private String name;
    
    private String email;
    
    private String password;
    
    private boolean status;
    
    
    public Member dtoToEntity() {
        return Member.builder()
                .id(id)
                .name(name)
                .email(email)
                .password(password)
                .status(status)
                .build();
    }
    
    @Builder
    public MemberUpdateRequestDto(Long id, String name, String email, String password,
            String matchingPassword, boolean status) {
        
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.status = status;
    }
}
