package proj.chat.domain.channel.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import proj.chat.domain.channel.dto.ChannelEnterRequestDto;

@Component
@RequiredArgsConstructor
public class ChannelEnterRequestDtoValidator implements Validator {
    
    @Override
    public boolean supports(Class<?> clazz) {
        
        return clazz.isAssignableFrom(ChannelEnterRequestDto.class);
    }
    
    @Override
    public void validate(Object target, Errors errors) {
        
        ChannelEnterRequestDto dto = (ChannelEnterRequestDto) target;
        
        
    }
}
