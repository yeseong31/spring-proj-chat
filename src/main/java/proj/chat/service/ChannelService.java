package proj.chat.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proj.chat.domain.channel.dto.ChannelResponseDto;
import proj.chat.domain.channel.dto.ChannelSaveRequestDto;
import proj.chat.domain.channel.entity.Channel;
import proj.chat.domain.member.entity.Member;
import proj.chat.exception.DataNotFoundException;
import proj.chat.domain.channel.repository.ChannelRepository;
import proj.chat.domain.member.repository.MemberRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChannelService {
    
    private final ChannelRepository channelRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 채널 생성
     *
     * @param dto 채널 정보가 담긴 DTO
     * @return 채널 생성 이후에 부여되는 채널 ID(인덱스)
     */
    @Transactional
    public String save(ChannelSaveRequestDto dto) {
        
        Channel channel = dto.dtoToEntity();
        
        // 비밀번호가 설정되었다면 암호화
        if (dto.getPassword() != null) {
            channel.hashPassword(passwordEncoder);
        }
        
        // 채널 UUID 생성
        channel.createUUID();
    
        Member findMember = memberRepository.findByEmail(dto.getMemberEmail())
                .orElseThrow(() -> new DataNotFoundException("존재하지 않는 사용자입니다"));
    
        channel.addMember(findMember);
        
        // TODO: 이미 방에 참가 중인 사람에 대해서는 저장을 하면 안 됨
        //  지금 Member와 Channel이 1:N 관계라서 하나의 계정에 대해 채널을 여러 개 설정할 수 없음
        
        return channelRepository.save(channel).getUuid();
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
