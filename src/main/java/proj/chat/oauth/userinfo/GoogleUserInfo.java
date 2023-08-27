package proj.chat.oauth.userinfo;

import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GoogleUserInfo implements OAuth2UserInfo {
    
    private final Map<String, Object> attributes;
    
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    
    @Override
    public String getProviderId() {
        return attributes.get("sub").toString();
    }
    
    @Override
    public String getProvider() {
        return "google";
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
