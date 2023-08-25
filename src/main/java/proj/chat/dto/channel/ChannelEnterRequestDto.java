package proj.chat.dto.channel;

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
    
    private String channelId;
    
    @NotEmpty(message = "비밀번호를 입력해 주세요")
    private String password;
}
