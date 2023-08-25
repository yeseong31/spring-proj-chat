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
    
    private String uuid;
    
    private String email;
    
    private String password;
    
    private boolean fromSocial;
    
    private boolean status;
    
    
    public Member dtoToEntity() {
        return Member.builder()
                .id(id)
                .name(name)
                .uuid(uuid)
                .email(email)
                .password(password)
                .fromSocial(fromSocial)
                .status(status)
                .build();
    }
    
    @Builder
    public MemberUpdateRequestDto(
            Long id, String name, String uuid, String email,
            String password, boolean fromSocial, boolean status) {
        
        this.id = id;
        this.name = name;
        this.uuid = uuid;
        this.email = email;
        this.password = password;
        this.fromSocial = fromSocial;
        this.status = status;
    }
}
