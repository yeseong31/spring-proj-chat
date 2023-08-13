package proj.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proj.chat.dto.chat.MessageDto;
import proj.chat.domain.channel.entity.Channel;
import proj.chat.entity.Message;
import proj.chat.repository.ChannelRepository;
import proj.chat.repository.MessageRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MessageService {
    
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    
    /**
     * 메시지 저장
     *
     * @param dto 메시지 정보가 담긴 DTO
     * @return 메시지 저장 이후에 부여되는 메시지 ID(인덱스)
     */
    @Transactional
    public Long save(MessageDto dto) {
        
        // 채널 UUID가 전달되지 않은 경우
        Channel channel = channelRepository.findByUuid(dto.getChannelUuid())
                .orElseThrow(() -> new IllegalArgumentException("잘못된 채널 UUID입니다"));
        
        // TODO: 메시지 저장 시 사용자 정보 포함
        Message message = Message.builder()
                .content(dto.getContent())
                .channel(channel)
                .build();
        
        return messageRepository.save(message).getId();
    }
}
