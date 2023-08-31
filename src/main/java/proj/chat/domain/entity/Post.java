package proj.chat.domain.entity;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@ToString(exclude = "member")
public class Post extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "post_id")
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Lob
    private String content;
    
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    
    @OneToMany(cascade = ALL, orphanRemoval = true)
    @JoinColumn(name = "comment_id")
    private final List<Comment> comments = new ArrayList<>();
    
    private int voters;
    
    @Builder
    public Post(Long id, String title, String content, Member member) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.member = member;
        this.voters = 0;
    }
    
    /**
     * 사용자 정보 등록
     *
     * @param member 인증 정보를 소유할 사용자
     */
    public void registerMember(Member member) {
        this.member = member;
    }
    
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
