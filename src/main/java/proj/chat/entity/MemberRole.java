package proj.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberRole {
    MEMBER("MEMBER"),
    ADMIN("MEMBER,ADMIN");
    
    private final String value;
}
