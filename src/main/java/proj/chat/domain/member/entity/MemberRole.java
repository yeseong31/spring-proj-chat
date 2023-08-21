package proj.chat.domain.member.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberRole {
    MEMBER("ROLE_MEMBER"),
    ADMIN("ROLE_MEMBER, ROLE_ADMIN");
    
    private final String value;
}
