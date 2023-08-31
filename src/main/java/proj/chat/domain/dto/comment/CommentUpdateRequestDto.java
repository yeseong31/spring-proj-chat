package proj.chat.domain.dto.comment;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentUpdateRequestDto {
    
    private String content;
    
    @Builder
    public CommentUpdateRequestDto(String content) {
        this.content = content;
    }
}
