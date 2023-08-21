package proj.chat.domain.channel.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ChannelEnterRequestDto {
    
    private String channelId;
    
    @Length(min = 4, max = 16, message = "비밀번호는 4~16자여야 합니다")
    @NotEmpty(message = "비밀번호를 입력해 주세요")
    private String password;
}
