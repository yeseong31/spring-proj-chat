package proj.chat.service;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proj.chat.dto.MemberResponseDto;
import proj.chat.dto.MemberSaveRequestDto;
import proj.chat.entity.Member;
import proj.chat.exception.DataNotFoundException;
import proj.chat.exception.DuplicatedMemberEmailException;
import proj.chat.exception.NotValidateEmailException;
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
     * @param dto 회원정보가 담긴 DTO
     * @return 회원가입 이후에 부여되는 사용자 ID(인덱스)
     */
    @Transactional
    public Long save(MemberSaveRequestDto dto) {
        
        // 사용자 검증
        validateMember(dto.getEmail());
        
        Member member = dto.dtoToEntity();
        
        // 비밀번호 암호화
        member.hashPassword(passwordEncoder);
        
        return memberRepository.save(member).getId();
    }
    
    /**
     * 사용자 조회
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
     * @param email 사용자 이메일
     * @return 사용자 정보가 담긴 DTO
     */
    public MemberResponseDto findByEmail(String email) {
        
        Member findMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new DataNotFoundException("존재하지 않는 회원입니다"));
        
        return new MemberResponseDto(findMember);
    }
    
    /**
     * 사용자 검증
     * @param email 검증할 사용자 이메일
     */
    private void validateMember(String email) {
        
        validateDuplicateEmail(email);
        validateEmailForm(email);
    }
    
    /**
     * 사용자 검증 - 이메일 중복 검증
     * @param email 검증할 사용자 이메일
     */
    private void validateDuplicateEmail(String email) {
        
        Optional<Member> result = memberRepository.findByEmail(email);
        
        if (result.isPresent()) {
            throw new DuplicatedMemberEmailException("이미 존재하는 회원입니다");
        }
    }
    
    /**
     * 사용자 검증 - 이메일 형식 검증
     * @param email 검증할 사용자 이메일
     */
    private void validateEmailForm(String email) {
        
        String regex = "[a-z0-9]+@[a-z]+\\.[a-z]{2,3}";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        
        if (!m.matches()) {
            throw new NotValidateEmailException("잘못된 형식의 이메일입니다");
        }
    }
}
