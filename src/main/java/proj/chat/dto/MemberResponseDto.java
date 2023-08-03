package proj.chat.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import proj.chat.entity.Member;

@Getter
@NoArgsConstructor
public class MemberResponseDto {
    
    private Long id;
    private String name;
    private String email;
    private String password;
    
    public MemberResponseDto(Member entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.email = entity.getEmail();
        this.password = entity.getPassword();
    }
}