package proj.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proj.chat.dto.member.MemberResponseDto;
import proj.chat.dto.member.MemberSaveRequestDto;
import proj.chat.dto.member.MemberUpdateRequestDto;
import proj.chat.entity.Member;
import proj.chat.repository.MemberRepository;
import proj.chat.exception.DataNotFoundException;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 회원가입을 진행한다.
     *
     * @param dto 회원정보가 담긴 DTO
     * @return 회원가입 이후에 부여되는 사용자 ID(인덱스)
     */
    @Transactional
    public Long save(MemberSaveRequestDto dto) {
        
        Member member = dto.dtoToEntity();
        
        if (dto.getPassword() != null) {
            member.hashPassword(passwordEncoder);
        }
        
        member.createUUID();
        
        return memberRepository.save(member).getId();
    }
    
    /**
     * ID(인덱스)로 사용자를 조회한다.
     *
     * @param id 사용자 ID(인덱스)
     * @return 사용자 정보가 담긴 DTO
     */
    public MemberResponseDto findById(Long id) {
        
        Member findMember = memberRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("존재하지 않는 회원입니다"));
        
        return new MemberResponseDto(findMember);
    }
    
    /**
     * 이메일로 사용자를 조회한다.
     *
     * @param email 사용자 이메일
     * @return 사용자 정보가 담긴 DTO
     */
    public MemberResponseDto findByEmail(String email) {
        
        Member findMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("존재하지 않는 회원입니다"));
        
        return new MemberResponseDto(findMember);
    }
    
    /**
     * UUID로 사용자를 조회한다.
     *
     * @param uuid 사용자 이메일
     * @return 사용자 정보가 담긴 DTO
     */
    public MemberResponseDto findByUuid(String uuid) {
        
        Member findMember = memberRepository.findByUuid(uuid)
                .orElseThrow(() -> new DataNotFoundException("존재하지 않는 회원입니다"));
        
        return new MemberResponseDto(findMember);
    }
    
    /**
     * 사용자 정보를 수정한다.
     *
     * @param id         사용자 ID(인덱스)
     * @param requestDto 사용자 정보가 담긴 DTO
     * @return 사용자 ID(인덱스)
     */
    @Transactional
    public Long update(Long id, MemberUpdateRequestDto requestDto) {
        
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("존재하지 않는 회원입니다"));
        
        requestDto.setId(member.getId());
        
        if (requestDto.getName() == null) {
            requestDto.setName(member.getName());
        }
        if (requestDto.getEmail() == null) {
            requestDto.setEmail(member.getEmail());
        }
        if (requestDto.getPassword() == null) {
            requestDto.setPassword(member.getPassword());
        }
        if (requestDto.getUuid() == null) {
            requestDto.setUuid(member.getUuid());
        }
    
        requestDto.setFromSocial(requestDto.isFromSocial());
        requestDto.setStatus(true);
        
        memberRepository.save(requestDto.dtoToEntity());
        
        return id;
    }
    
    /**
     * 사용자 정보를 삭제한다.
     *
     * @param id 사용자 ID(인덱스)
     * @return 사용자 ID(인덱스)
     */
    @Transactional
    public Long delete(Long id) {
        
        memberRepository.deleteById(id);
        return id;
    }
}
