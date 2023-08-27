package proj.chat.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FromSocial {
    
    KAKAO("KAKAO"),
    NONE("NONE");
    
    private final String value;
}
