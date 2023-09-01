package proj.chat.domain.service;

import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import proj.chat.domain.dto.message.MessageDto;
import proj.chat.domain.repository.message.MessageRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {
    
    private final MessageRepository messageRepository;
    
    public List<MessageDto> findByChannelUuid(String uuid) {
    
        return messageRepository.findByChannelUuid(uuid).stream()
                .map(MessageDto::new)
                .sorted(Comparator.comparing(MessageDto::getCreatedDate).reversed()).toList();
    }
    
    public String save(MessageDto messageDto) {
    
        return messageRepository.save(messageDto.dtoToEntity()).getId();
    }
}
