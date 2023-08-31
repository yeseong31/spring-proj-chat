package proj.chat.domain.dto.voter;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VoterCommentResponseDto {
    
    private Long id;
    private Long commentId;
    private Long memberId;
    
    public VoterCommentResponseDto(Long id, Long commentId, Long memberId) {
        this.id = id;
        this.commentId = commentId;
        this.memberId = memberId;
    }
}
