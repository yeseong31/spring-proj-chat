package proj.chat.entity;

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

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@ToString(exclude = {"verificationToken", "verificationTokenTime", "member"})
public class EmailToken {
    
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "emailtoken_id")
    private Long id;
    
    @Column(name = "verification_token")
    private String verificationToken;
    
    @Column(name = "verification_token_time")
    private LocalDateTime verificationTokenTime;
    
    @Column(columnDefinition = "boolean default false")
    private boolean expired;
    
    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;
    
    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    
    @Builder
    public EmailToken(String verificationToken, LocalDateTime verificationTokenTime,
            LocalDateTime expirationDate, Member member) {
        
        this.verificationToken = verificationToken;
        this.verificationTokenTime = verificationTokenTime;
        this.expirationDate = expirationDate;
        this.member = member;
        this.expired = false;
    }
    
    public static EmailToken generateVerificationToken(Member member) {
        return EmailToken.builder()
                .member(member)
                .verificationToken(UUID.randomUUID().toString())
                .verificationTokenTime(LocalDateTime.now())
                .expirationDate(LocalDateTime.now().plusMinutes(5L))
                .build();
    }
    
    public void expiredToken() {
        this.expired = true;
    }
}
