package proj.chat.domain.dto.voter;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class VoterPostRequestDto {
    
    private Long postId;
    private Long memberId;
    
    @Builder
    public VoterPostRequestDto(Long postId, Long memberId) {
        this.postId = postId;
        this.memberId = memberId;
    }
}
