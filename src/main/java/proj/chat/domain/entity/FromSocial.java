package proj.chat.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FromSocial {
    
    GOOGLE("google"),
    KAKAO("kakao"),
    NAVER("naver"),
    NONE("none");
    
    private final String value;
}
