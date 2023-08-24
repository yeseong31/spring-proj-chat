package proj.chat.oauth.context;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import proj.chat.domain.member.entity.Member;

@Getter
public class MemberContext extends User implements OAuth2User {
    
    private final Long id;
    private final String email;
    private Map<String, Object> attributes;
    private String userNameAttributeName;
    
    // TODO 카카오 로그인 + Spring Security
    
    public MemberContext(Member member, List<GrantedAuthority> authorities) {
        super(member.getName(), member.getPassword(), authorities);
        this.id = member.getId();
        this.email = member.getEmail();
    }
    
    public MemberContext(Member member, List<GrantedAuthority> authorities,
            Map<String, Object> attributes, String userNameAttributeName) {
    
        this(member, authorities);
        this.attributes = attributes;
        this.userNameAttributeName = userNameAttributeName;
    }
    
    @Override
    public String getName() {
        return getAttribute(userNameAttributeName);
    }
    
    @Override
    public Set<GrantedAuthority> getAuthorities() {
        return new HashSet<>(super.getAuthorities());
    }
    
    @Override
    public Map<String, Object> getAttributes() {
        return this.attributes;
    }
}
