package proj.chat.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import proj.chat.domain.member.dto.MemberResponseDto;
import proj.chat.domain.member.service.MemberService;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    
    private final MemberService memberService;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        MemberResponseDto dto = memberService.findByEmail(email);
        
        return User.builder()
                .username(dto.getEmail())
                .password(dto.getPassword())
                .build();
    }
}
