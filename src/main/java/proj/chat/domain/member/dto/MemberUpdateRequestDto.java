package proj.chat.domain.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import proj.chat.domain.member.entity.Member;

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
    
    private boolean status;
    
    
    public Member dtoToEntity() {
        return Member.builder()
                .id(id)
                .name(name)
                .uuid(uuid)
                .email(email)
                .password(password)
                .status(status)
                .build();
    }
    
    @Builder
    public MemberUpdateRequestDto(
            Long id, String name, String uuid, String email,
            String password, String matchingPassword, boolean status) {
        
        this.id = id;
        this.name = name;
        this.uuid = uuid;
        this.email = email;
        this.password = password;
        this.status = status;
    }
}