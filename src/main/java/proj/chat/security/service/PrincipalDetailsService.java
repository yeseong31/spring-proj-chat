package proj.chat.security.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import proj.chat.domain.dto.member.MemberResponseDto;
import proj.chat.domain.entity.MemberRole;
import proj.chat.domain.service.MemberService;

@Component
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {
    
    private final MemberService memberService;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        MemberResponseDto dto = memberService.findByEmail(email);
    
        if (dto == null) {
            return null;
        }
    
        List<GrantedAuthority> authorityList = new ArrayList<>();
        if (dto.getName().equals("admin")) {
            authorityList.add(new SimpleGrantedAuthority(MemberRole.ADMIN.getValue()));
        } else {
            authorityList.add(new SimpleGrantedAuthority(MemberRole.MEMBER.getValue()));
        }
        
        return User.builder()
                .username(dto.getEmail())
                .password(dto.getPassword())
                .authorities(authorityList)
                .build();
    }
}