package proj.chat.domain.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import proj.chat.domain.entity.Comment;

@Getter
@Setter
@NoArgsConstructor
public class CommentSaveRequestDto {
    
    @NotBlank(message = "내용을 입력해주세요")
    private String content;
    
    public Comment dtoToEntity() {
        return Comment.builder()
                .content(content)
                .build();
    }
    
    @Builder
    public CommentSaveRequestDto(String content) {
        this.content = content;
    }
}
