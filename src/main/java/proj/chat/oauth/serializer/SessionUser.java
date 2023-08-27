package proj.chat.oauth.serializer;

import java.io.Serializable;
import lombok.Getter;
import proj.chat.domain.entity.Member;

@Getter
public class SessionUser implements Serializable {
    
    private String name;
    private String email;
    
    public SessionUser(Member member){
        this.name = member.getName();
        this.email = member.getEmail();
    }
}
