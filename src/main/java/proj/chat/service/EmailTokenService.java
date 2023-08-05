package proj.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proj.chat.dto.EmailVerificationRequestDto;
import proj.chat.dto.EmailVerificationResponseDto;
import proj.chat.entity.EmailToken;
import proj.chat.entity.Member;
import proj.chat.exception.DataNotFoundException;
import proj.chat.repository.EmailTokenRepository;
import proj.chat.repository.MemberRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EmailTokenService {
    
    private final MemberRepository memberRepository;
    private final EmailTokenRepository emailTokenRepository;
    private final PasswordEncoder passwordEncoder;
    
    /**
     * 이메일 인증 정보 저장
     * @param dto 이메일 인증 정보를 담은 DTO
     * @return 인증 정보 저장 이후에 부여되는 ID(인덱스)
     */
    @Transactional
    public Long save(EmailVerificationRequestDto dto) {
        // 사용자 정보 확인
        Member findMember = memberRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new DataNotFoundException("존재하지 않는 회원입니다"));
    
        EmailToken emailToken = dto.dtoToEntity();
        emailToken.registerMember(findMember);
        
        log.info("email: {}", emailToken.getMember());
        log.info("token: {}", emailToken.getToken());
    
        // 인증 코드 암호화
        emailToken.hashVerificationToken(passwordEncoder);
    
        log.info("token: {}", emailToken.getToken());
        
        return emailTokenRepository.save(emailToken).getId();
    }
    
    /**
     * 사용자의 인증 정보 조회
     * @param memberId 조회할 사용자의 ID(인덱스)
     * @return 인증 정보가 담긴 DTO
     */
    public EmailVerificationResponseDto findByMemberId(Long memberId) {
        EmailToken findEmailToken = emailTokenRepository.findByMemberId(memberId)
                .orElseThrow(() -> new DataNotFoundException("회원가입을 먼저 진행해주세요"));
    
        return new EmailVerificationResponseDto(findEmailToken);
    }
}
