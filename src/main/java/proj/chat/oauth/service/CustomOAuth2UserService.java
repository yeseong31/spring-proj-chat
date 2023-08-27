package proj.chat.oauth.service;

import static proj.chat.domain.entity.FromSocial.GOOGLE;
import static proj.chat.domain.entity.FromSocial.NONE;
import static proj.chat.domain.entity.MemberRole.MEMBER;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import proj.chat.domain.entity.FromSocial;
import proj.chat.domain.entity.Member;
import proj.chat.domain.repository.MemberRepository;
import proj.chat.oauth.dto.OAuthAttributes;
import proj.chat.security.auth.CustomUserDetails;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    
    private final MemberRepository memberRepository;
    
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        
        OAuth2User oAuth2User = super.loadUser(userRequest);
        
        String provider = userRequest.getClientRegistration().getRegistrationId();
        String providerId = oAuth2User.getAttribute("sub");
        
        String memberName = provider + "_" + providerId;
        String uuid = UUID.randomUUID().toString();
        String email = oAuth2User.getAttribute("email");
        
        Optional<Member> findMember = memberRepository.findByName(memberName);
        if (findMember.isPresent()) {
            return new CustomUserDetails(findMember.get(), oAuth2User.getAttributes());
        }
        
        FromSocial fromSocial;
        if (provider.equals("google")) {
            fromSocial = GOOGLE;
        } else {
            fromSocial = NONE;
        }
        
        Member member = Member.builder()
                .name(memberName)
                .uuid(uuid)
                .email(email)
                .role(MEMBER)
                .fromSocial(fromSocial)
                .status(true)
                .build();
    
        memberRepository.save(member);
        
        return new CustomUserDetails(member, oAuth2User.getAttributes());
    }
    
    private Member saveOrUpdate(OAuthAttributes attributes) {
        Member member = memberRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName()))
                .orElse(attributes.toEntity());
        
        return memberRepository.save(member);
    }
}
