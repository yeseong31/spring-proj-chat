package proj.chat.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import proj.chat.domain.dto.member.MemberResponseDto;
import proj.chat.domain.entity.Member;
import proj.chat.domain.service.MemberService;
import proj.chat.security.auth.CustomUserDetails;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final MemberService memberService;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        MemberResponseDto dto = memberService.findByEmail(email);
        
        if (dto == null) {
            return null;
        }
        
        Member member = Member.builder()
                .email(dto.getEmail())
                .name(dto.getName())
                .password(dto.getPassword())
                .role(dto.getRole())
                .uuid(dto.getUuid())
                .build();
        
        return new CustomUserDetails(member);
    }
}
