package proj.chat.domain.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@ToString(exclude = {"owner", "password"})
public class Channel extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "channel_id")
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String uuid;
    
    @Column(nullable = false)
    private String name;
    
    private int count;
    
    private int maxCount;
    
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member owner;
    
    @Column(nullable = false)
    private String password;
    
    @Builder
    public Channel(Long id, String uuid, String name, int count, int maxCount, Member owner, String password) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.count = count;
        this.maxCount = maxCount;
        this.owner = owner;
        this.password = password;
    }
    
    /**
     * 채널 생성자 등록
     */
    public void registerOwner(Member owner) {
        this.owner = owner;
    }
    
    /**
     * 채널 UUID 생성
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
     * 채널 인원 증가
     */
    public void increaseCount() {
        if (count == maxCount) {
            throw new IllegalStateException("정원이 가득 찼습니다");
        }
        count += 1;
    }
    
    /**
     * 채널 인원 감소
     */
    public void decreaseCount() {
        if (count == 0) {
            throw new IllegalStateException("빈 방입니다");
        }
        count -= 1;
    }
}
