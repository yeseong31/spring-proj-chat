package proj.chat.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proj.chat.aop.annotation.Trace;
import proj.chat.domain.member.dto.EmailVerificationRequestDto;
import proj.chat.domain.member.dto.EmailVerificationResponseDto;
import proj.chat.domain.member.entity.EmailToken;
import proj.chat.domain.member.entity.Member;
import proj.chat.domain.member.repository.EmailTokenRepository;
import proj.chat.domain.member.repository.MemberRepository;
import proj.chat.exception.DataNotFoundException;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EmailTokenService {
    
    private final MemberRepository memberRepository;
    private final EmailTokenRepository emailTokenRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 이메일 인증 정보를 저장한다.
     *
     * @param dto 이메일 인증 정보를 담은 DTO
     * @return 인증 정보 저장 이후에 부여되는 ID(인덱스)
     */
    @Trace
    @Transactional
    public Long save(EmailVerificationRequestDto dto) {
        
        Member findMember = memberRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new DataNotFoundException("존재하지 않는 회원입니다"));
        
        EmailToken emailToken = dto.dtoToEntity();
        emailToken.registerMember(findMember);
        
        log.info("email: {}", emailToken.getMember());
        log.info("token: {}", emailToken.getToken());
        
        emailToken.hashVerificationToken(passwordEncoder);
        
        return emailTokenRepository.save(emailToken).getId();
    }
    
    /**
     * 이메일 인증 정보를 조회한다.
     *
     * @param memberId 조회하고자 하는 사용자 ID(인덱스)
     * @return 사용자 인증 정보를 담은 DTO
     */
    public EmailVerificationResponseDto findByMemberId(Long memberId) {
        
        EmailToken findEmailToken = emailTokenRepository.findByMemberId(memberId)
                .orElseThrow(() -> new DataNotFoundException("인증 정보가 존재하지 않습니다"));
        
        return new EmailVerificationResponseDto(findEmailToken);
    }
    
    /**
     * 인증 토큰 일치 여부를 확인한다.
     *
     * @param memberId 인증 정보를 가지는 사용자 ID(인덱스)
     * @param token    토큰 정보
     * @return 인증 토큰 일치 여부
     */
    public boolean checkToken(Long memberId, String token) {
        
        EmailToken findEmailToken = emailTokenRepository.findByMemberId(memberId)
                .orElseThrow(() -> new DataNotFoundException("인증 정보가 존재하지 않습니다"));
        
        return findEmailToken.checkVerificationToken(token, passwordEncoder);
    }
}
