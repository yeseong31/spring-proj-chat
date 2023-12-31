package proj.chat.domain.entity;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "MEMBER", uniqueConstraints = {@UniqueConstraint(
        name = "UUID_EMAIL_UNIQUE",
        columnNames = {"uuid", "email"}
)})
@Getter
@NoArgsConstructor(access = PROTECTED)
@ToString(exclude = "password")
public class Member extends BaseTimeEntity {
    
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "member_id")
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String uuid;
    
    @Column(nullable = false)
    private String email;
    
    private String password;
    
    @Enumerated(STRING)
    private FromSocial fromSocial;
    
    @Enumerated(STRING)
    private MemberRole role;
    
    @Column(columnDefinition = "boolean default false")
    private boolean status;
    
    @Builder
    public Member(Long id, String name, String uuid, String email,
            String password, FromSocial fromSocial, MemberRole role, boolean status) {
        
        this.id = id;
        this.name = name;
        this.uuid = uuid;
        this.email = email;
        this.password = password;
        this.fromSocial = fromSocial;
        this.role = role;
        this.status = status;
    }
    
    /**
     * 사용자 UUID 생성
     */
    public void createUUID() {
        uuid = UUID.randomUUID().toString();
    }
    
    /**
     * 비밀번호 암호화
     *
     * @param passwordEncoder 암호화에 사용할 인코더 클래스
     */
    public void hashPassword(PasswordEncoder passwordEncoder) {
        password = passwordEncoder.encode(password);
    }
    
    /**
     * 비밀번호 일치 확인
     *
     * @param plainPassword   암호화되지 않은 비밀빈호
     * @param passwordEncoder 암호화에 사용한 인코더 클래스
     * @return 비밀번호 일치 여부(true/false)
     */
    public boolean checkPassword(String plainPassword, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(plainPassword, password);
    }
    
    /**
     * 사용자 정보 업데이트
     *
     * @param name 수정할 이름
     * @return 사용자(Member)
     */
    public Member update(String name) {
        this.name = name;
        return this;
    }
}
