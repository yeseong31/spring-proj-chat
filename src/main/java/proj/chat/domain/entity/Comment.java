package proj.chat.domain.entity;

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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@ToString(exclude = {"post", "member"})
public class Comment extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "comment_id")
    private Long id;
    
    @Lob
    private String content;
    
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;
    
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    
    private int voters;
    
    @Builder
    public Comment(String content, Post post, Member member) {
        this.content = content;
        this.post = post;
        this.member = member;
        this.voters = 0;
    }
    
    public void addComment() {
        post.getComments().add(this);
    }
    
    public void removeComment() {
        post.getComments().remove(this);
    }
    
    public void update(String content) {
        this.content = content;
    }
    
    public void registerMember(Member member) {
        this.member = member;
    }
}
