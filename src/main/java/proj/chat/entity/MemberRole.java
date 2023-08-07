package proj.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberRole {
    GUEST("GUEST"),
    MEMBER("GUEST,MEMBER"),
    ADMIN("GUEST,MEMBER,ADMIN");
    
    private final String value;
}
