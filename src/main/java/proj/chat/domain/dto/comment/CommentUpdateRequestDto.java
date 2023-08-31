package proj.chat.domain.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentUpdateRequestDto {
    
    @NotBlank(message = "내용을 입력해주세요")
    private String content;
    
    @Builder
    public CommentUpdateRequestDto(String content) {
        this.content = content;
    }
}
