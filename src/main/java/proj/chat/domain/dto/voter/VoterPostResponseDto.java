package proj.chat.domain.dto.voter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import proj.chat.domain.entity.VoterPost;

@Getter
@NoArgsConstructor
public class VoterPostResponseDto {
    
    private Long id;
    private Long postId;
    private Long memberId;
    
    public VoterPostResponseDto(VoterPost entity) {
        this.id = entity.getId();
        this.postId = entity.getPost().getId();
        this.memberId = entity.getMember().getId();
    }
}
