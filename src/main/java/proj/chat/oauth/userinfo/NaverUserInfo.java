package proj.chat.oauth.userinfo;

import java.util.Map;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NaverUserInfo implements OAuth2UserInfo {
    
    private Map<String, Object> attributes;
    
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    
    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }
    
    @Override
    public String getProvider() {
        return "naver";
    }
    
    @Override
    public String getEmail() {
        return attributes.get("email").toString();
    }
    
    @Override
    public String getName() {
        return attributes.get("name").toString();
    }
}
