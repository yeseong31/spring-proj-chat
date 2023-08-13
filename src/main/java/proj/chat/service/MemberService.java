package proj.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proj.chat.domain.member.dto.MemberResponseDto;
import proj.chat.domain.member.dto.MemberSaveRequestDto;
import proj.chat.domain.member.dto.MemberUpdateRequestDto;
import proj.chat.entity.Member;
import proj.chat.exception.DataNotFoundException;
import proj.chat.repository.MemberRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 사용자 저장(회원가입)
     *
     * @param dto 회원정보가 담긴 DTO
     * @return 회원가입 이후에 부여되는 사용자 ID(인덱스)
     */
    @Transactional
    public String save(MemberSaveRequestDto dto) {
        
        Member member = dto.dtoToEntity();
        
        // 비밀번호 암호화
        member.hashPassword(passwordEncoder);
        
        // 사용자 UUID 생성
        member.createUUID();
        
        return memberRepository.save(member).getUuid();
    }
    
    /**
     * 사용자 조회
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
     * 사용자 이메일로 조회
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
     * 사용자 UUID로 조회
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
     * 사용자 수정
     * @param id 사용자 ID(인덱스)
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
        requestDto.setStatus(true);
        
        memberRepository.save(requestDto.dtoToEntity());
        
        return id;
    }
    
    /**
     * 사용자 삭제
     * @param id 사용자 ID(인덱스)
     * @return 사용자 ID(인덱스)
     */
    @Transactional
    public Long delete(Long id) {
        memberRepository.deleteById(id);
        return id;
    }
}
