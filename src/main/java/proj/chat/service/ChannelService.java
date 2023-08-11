package proj.chat.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proj.chat.dto.chat.ChannelResponseDto;
import proj.chat.dto.chat.ChannelSaveRequestDto;
import proj.chat.entity.Channel;
import proj.chat.exception.DataNotFoundException;
import proj.chat.repository.ChannelRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChannelService {
    
    private final ChannelRepository channelRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 채널 생성
     *
     * @param dto 채널 정보가 담긴 DTO
     * @return 채널 생성 이후에 부여되는 채널 ID(인덱스)
     */
    @Transactional
    public Long save(ChannelSaveRequestDto dto) {
        
        Channel channel = dto.dtoToEntity();
        
        // 비밀번호가 설정되었다면 암호화
        if (dto.getPassword() != null) {
            channel.hashPassword(passwordEncoder);
        }
        
        // 채널 UUID 생성
        channel.createUUID();
        
        return channelRepository.save(channel).getId();
    }
    
    /**
     * 채널 목록 조회
     *
     * @return 채널 정보가 담긴 DTO 목록
     */
    public List<ChannelResponseDto> findAll() {
        return channelRepository.findAll().stream()
                .map(ChannelResponseDto::new)
                .collect(Collectors.toList());
    }
    
    /**
     * 채널 조회
     *
     * @param id 채널 ID(인덱스)
     * @return 채널 정보가 담긴 DTO
     */
    public ChannelResponseDto findById(Long id) {
        
        Channel findChannel = channelRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("존재하지 않는 채널입니다"));
        
        return new ChannelResponseDto(findChannel);
    }
    
    /**
     * 채널 UUID로 조회
     *
     * @param uuid 채널 UUID
     * @return 채널 정보가 담긴 DTO
     */
    public ChannelResponseDto findByUuid(String uuid) {
        
        Channel findChannel = channelRepository.findByUuid(uuid)
                .orElseThrow(() -> new DataNotFoundException("존재하지 않는 채널입니다"));
        
        return new ChannelResponseDto(findChannel);
    }
}
