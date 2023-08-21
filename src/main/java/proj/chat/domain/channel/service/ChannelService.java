package proj.chat.domain.channel.service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proj.chat.domain.channel.dto.ChannelResponseDto;
import proj.chat.domain.channel.dto.ChannelSaveRequestDto;
import proj.chat.domain.channel.entity.Channel;
import proj.chat.domain.channel.repository.ChannelRepository;
import proj.chat.domain.member.entity.Member;
import proj.chat.domain.member.repository.MemberRepository;
import proj.chat.exception.DataNotFoundException;

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
    public String save(ChannelSaveRequestDto dto, String memberEmail) {
        
        Member findMember = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new DataNotFoundException("존재하지 않는 사용자입니다"));
        
        Channel channel = dto.dtoToEntity();
        
        Objects.requireNonNull(channel.getPassword());
        channel.hashPassword(passwordEncoder);
        
        channel.createUUID();
        channel.registerOwner(findMember);
        
        return channelRepository.save(channel).getUuid();
    }
    
    /**
     * 채널 목록 조회 (+페이징/검색)
     *
     * @return 채널 정보가 담긴 DTO 목록
     */
    public Page<ChannelResponseDto> findAll(String keyword, int page, int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        
        List<ChannelResponseDto> result = channelRepository.findSearch(keyword).stream()
                .map(ChannelResponseDto::new)
                .sorted(Comparator.comparing(ChannelResponseDto::getCreatedDate).reversed())
                .collect(Collectors.toList());
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), result.size());
        
        List<ChannelResponseDto> pageContent = result.subList(start, end);
        
        return new PageImpl<>(pageContent, pageable, result.size());
    }
    
    /**
     * 채널 ID로 조회
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
    
    /**
     * 채널 UUID로 채널 존재 여부 확인
     *
     * @param uuid 채널 UUID
     * @return 채널 존재 여부
     */
    public boolean existsByUuid(String uuid) {
        return channelRepository.existsByUuid(uuid);
    }
    
    /**
     * 비밀번호 일치 여부 확인
     *
     * @param channelId 채널 ID(인덱스)
     * @param password  비밀번호
     * @return 비밀번호 일치 여부
     */
    public boolean checkPassword(Long channelId, String password) {
        
        Channel findChannel = channelRepository.findById(channelId)
                .orElseThrow(() -> new DataNotFoundException("채널을 찾을 수 없습니다"));
        
        return findChannel.checkPassword(password, passwordEncoder);
    }
}
