package proj.chat.domain.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import proj.chat.domain.entity.Comment;
import proj.chat.domain.entity.Member;
import proj.chat.domain.entity.Post;

@Getter
@NoArgsConstructor
public class CommentSaveRequestDto {
    
    @NotBlank(message = "내용을 입력해주세요")
    private String content;
    
    private Member member;
    
    private Post post;
    
    public Comment dtoToEntity() {
        return Comment.builder()
                .content(content)
                .member(member)
                .post(post)
                .build();
    }
    
    @Builder
    public CommentSaveRequestDto(String content, Member member, Post post) {
        this.content = content;
        this.member = member;
        this.post = post;
    }
}
