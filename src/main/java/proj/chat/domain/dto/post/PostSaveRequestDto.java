package proj.chat.domain.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import proj.chat.domain.entity.Member;
import proj.chat.domain.entity.Post;

@Getter
@NoArgsConstructor
public class PostSaveRequestDto {
    
    @NotBlank(message = "제목을 입력해주세요")
    private String title;
    
    @NotBlank(message = "내용을 입력해주세요")
    private String content;
    
    private Member member;
    
    public Post dtoToEntity() {
        return Post.builder()
                .title(title)
                .content(content)
                .member(member)
                .build();
    }
    
    @Builder
    public PostSaveRequestDto(String title, String content, Member member) {
        this.title = title;
        this.content = content;
        this.member = member;
    }
}
