package proj.chat.domain.entity;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@ToString(exclude = {"token", "tokenTime", "member"})
public class EmailToken {
    
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "emailtoken_id")
    private Long id;
    
    @Column(name = "token")
    private String token;
    
    @Column(name = "token_time")
    private LocalDateTime tokenTime;
    
    @Column(columnDefinition = "boolean default false")
    private boolean expired;
    
    @Column(name = "expiration_time")
    private LocalDateTime expirationTime;
    
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    
    @Builder
    public EmailToken(String token, LocalDateTime tokenTime,
            LocalDateTime expirationTime, Member member) {
        
        this.token = token;
        this.tokenTime = tokenTime;
        this.expirationTime = expirationTime;
        this.member = member;
        this.expired = false;
    }
    
    public static EmailToken generateVerificationToken(Member member) {
        return EmailToken.builder()
                .member(member)
                .token(UUID.randomUUID().toString())
                .tokenTime(LocalDateTime.now())
                .expirationTime(LocalDateTime.now().plusMinutes(5L))
                .build();
    }
    
    /**
     * 사용자 정보 등록
     * @param member 인증 정보를 소유할 사용자
     */
    public void registerMember(Member member) {
        this.member = member;
    }
    
    /**
     * 인증 코드 만료 처리
     */
    public void expiredToken() {
        this.expired = true;
    }
    
    /**
     * 인증 코드 만료 여부 확인
     * @return 인증 코드 만료 여부(true/false)
     */
    public boolean checkTokenTime() {
        return LocalDateTime.now().compareTo(tokenTime) > 0;
    }
    
    /**
     * 인증 코드 암호화
     * @param passwordEncoder 암호화에 사용할 인코더 클래스
     */
    public void hashVerificationToken(PasswordEncoder passwordEncoder) {
        token = passwordEncoder.encode(token);
    }
    
    /**
     * 인증 코드 일치 확인
     * @param plainToken 암호화되지 않은 인증 코드
     * @param passwordEncoder 암호화에 사용한 인코더 클래스
     * @return 인증 코드 일치 여부(true/false)
     */
    public boolean checkVerificationToken(String plainToken, PasswordEncoder passwordEncoder) {
        return passwordEncoder.matches(plainToken, token);
    }
}
