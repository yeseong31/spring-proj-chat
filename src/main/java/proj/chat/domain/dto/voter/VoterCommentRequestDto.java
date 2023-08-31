package proj.chat.domain.dto.voter;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VoterCommentRequestDto {
    
    private Long commentId;
    private Long memberId;
    
    @Builder
    public VoterCommentRequestDto(Long commentId, Long memberId) {
        this.commentId = commentId;
        this.memberId = memberId;
    }
}
