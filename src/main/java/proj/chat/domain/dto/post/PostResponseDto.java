package proj.chat.domain.dto.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import proj.chat.domain.entity.Post;

@Getter
@NoArgsConstructor
public class PostResponseDto {
    
    private Long id;
    private String title;
    private String content;
    private Long memberId;
    private String memberName;
    
    public PostResponseDto(Post entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.memberId = entity.getMember().getId();
        this.memberName = entity.getMember().getName();
    }
}
