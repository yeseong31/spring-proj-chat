package proj.chat.dto.chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import proj.chat.entity.Channel;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ChannelSaveRequestDto {
    
    @NotBlank(message = "이름을 입력해주세요")
    @Length(min = 3, max = 20)
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{3,20}$",
            message = "엉어, 숫자, 한글, 언더바(_), 대시(-)만 입력할 수 있습니다")
    private String name;
    
    private String password;
    
    private String memberEmail;
    
    @Range(min = 2, max = 20, message = "최대 인원은 2~20의 범위로 설정해 주세요")
    private int maxCount;
    
    public Channel dtoToEntity() {
        return Channel.builder()
                .name(name)
                .password(password)
                .count(0)
                .maxCount(maxCount)
                .build();
    }
    
    @Builder
    public ChannelSaveRequestDto(String name, String password, int maxCount) {
        this.name = name;
        this.password = password;
        this.maxCount = maxCount;
    }
}
