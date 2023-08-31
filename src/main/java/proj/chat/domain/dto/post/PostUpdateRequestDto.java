package proj.chat.domain.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostUpdateRequestDto {
    
    @NotBlank(message = "제목을 입력해주세요")
    private String title;
    
    @NotBlank(message = "내용을 입력해주세요")
    private String content;
    
    @Builder
    public PostUpdateRequestDto(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
