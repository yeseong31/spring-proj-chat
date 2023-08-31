package proj.chat.domain.dto.comment;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import proj.chat.domain.entity.Comment;

@Getter
@ToString
@NoArgsConstructor
public class CommentResponseDto {
    
    private Long id;
    private String content;
    private Long memberId;
    private String memberName;
    private String memberEmail;
    private Long postId;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private int voters;
    
    public CommentResponseDto(Comment entity) {
        this.id = entity.getId();
        this.content = entity.getContent();
        this.memberId = entity.getMember().getId();
        this.memberName = entity.getMember().getName();
        this.memberEmail = entity.getMember().getEmail();
        this.postId = entity.getPost().getId();
        this.createdDate = entity.getCreatedDate();
        this.lastModifiedDate = entity.getLastModifiedDate();
        this.voters = entity.getVoters();
    }
}
