package proj.chat.domain.dto.member;

import lombok.Getter;
import lombok.NoArgsConstructor;
import proj.chat.domain.entity.Member;
import proj.chat.domain.entity.MemberRole;

@Getter
@NoArgsConstructor
public class MemberResponseDto {
    
    private Long id;
    private String name;
    private String email;
    private String password;
    private String uuid;
    private MemberRole role;
    
    public MemberResponseDto(Member entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.email = entity.getEmail();
        this.password = entity.getPassword();
        this.uuid = entity.getUuid();
        this.role = entity.getRole();
    }
}
