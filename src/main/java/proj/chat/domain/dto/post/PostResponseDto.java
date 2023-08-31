package proj.chat.domain.dto.post;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import proj.chat.domain.dto.comment.CommentResponseDto;
import proj.chat.domain.entity.Post;

@Getter
@NoArgsConstructor
public class PostResponseDto {
    
    private Long id;
    private String title;
    private String content;
    private Long memberId;
    private String memberName;
    private String memberEmail;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private List<CommentResponseDto> comments;
    
    public PostResponseDto(Post entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.memberId = entity.getMember().getId();
        this.memberName = entity.getMember().getName();
        this.memberEmail = entity.getMember().getEmail();
        this.createdDate = entity.getCreatedDate();
        this.lastModifiedDate = entity.getLastModifiedDate();
        this.comments = entity.getComments().stream()
                .map(CommentResponseDto::new)
                .sorted(Comparator.comparing(CommentResponseDto::getCreatedDate).reversed())
                .collect(Collectors.toList());
        ;
    }
}
