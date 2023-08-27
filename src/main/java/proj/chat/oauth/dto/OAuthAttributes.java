package proj.chat.oauth.dto;

import static proj.chat.domain.entity.FromSocial.GOOGLE;
import static proj.chat.domain.entity.MemberRole.MEMBER;

import java.util.Map;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import proj.chat.domain.entity.Member;

@Getter
public class OAuthAttributes {
    
    private final Map<String, Object> attributes;
    private final String nameAttributeKey;
    private final String name;
    private final String email;
    
    @Builder
    public OAuthAttributes(
            Map<String, Object> attributes, String nameAttributeKey, String name, String email) {
        
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
    }
    
    public static OAuthAttributes of(
            String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        
        if ("google".equals(registrationId)) {
            return ofGoogle(userNameAttributeName, attributes);
        } else {
            return ofKakao("id", attributes);
        }
    }
    
    private static OAuthAttributes ofGoogle(String userNameAttributeName,
            Map<String, Object> attributes) {
        
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }
    
    private static OAuthAttributes ofKakao(String userNameAttributeName,
            Map<String, Object> attributes) {
        
        Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>)kakaoAccount.get("profile");
        
        return OAuthAttributes.builder()
                .name((String) kakaoProfile.get("nickname"))
                .email((String) kakaoAccount.get("email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }
    
    public Member toEntity() {
        
        return Member.builder()
                .name(name)
                .uuid(UUID.randomUUID().toString())
                .email(email)
                .fromSocial(GOOGLE)
                .role(MEMBER)
                .status(true)
                .build();
    }
}
