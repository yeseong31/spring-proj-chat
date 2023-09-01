package proj.chat.domain.dto.channel;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ChannelEnterRequestDto {
    
    private Long channelId;
    
    @NotEmpty(message = "비밀번호를 입력해 주세요")
    private String password;
}
