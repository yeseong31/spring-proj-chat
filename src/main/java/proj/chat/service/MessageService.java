package proj.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proj.chat.dto.chat.MessageDto;
import proj.chat.entity.Message;
import proj.chat.repository.MessageRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MessageService {
    
    private final MessageRepository messageRepository;
    
    /**
     * 메시지 저장
     *
     * @param dto 메시지 정보가 담긴 DTO
     * @return 메시지 저장 이후에 부여되는 메시지 ID(인덱스)
     */
    @Transactional
    public Long save(MessageDto dto) {
    
        Message message = dto.dtoToEntity();
        return messageRepository.save(message).getId();
    }
}
