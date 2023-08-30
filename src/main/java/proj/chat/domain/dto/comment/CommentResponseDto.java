package proj.chat.domain.dto.comment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import proj.chat.domain.entity.Comment;

@Getter
@NoArgsConstructor
public class CommentResponseDto {
    
    private Long id;
    private String content;
    private Long memberId;
    private String memberName;
    private Long postId;
    
    public CommentResponseDto(Comment entity) {
        this.id = entity.getId();
        this.content = entity.getContent();
        this.memberId = entity.getMember().getId();
        this.memberName = entity.getMember().getName();
        this.postId = entity.getPost().getId();
    }
}
