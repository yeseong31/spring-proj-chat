package proj.chat.oauth.service;

import static proj.chat.domain.entity.FromSocial.GOOGLE;
import static proj.chat.domain.entity.FromSocial.KAKAO;
import static proj.chat.domain.entity.FromSocial.NAVER;
import static proj.chat.domain.entity.FromSocial.NONE;
import static proj.chat.domain.entity.MemberRole.MEMBER;

import java.util.Map;
import java.util.Objects;
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
import proj.chat.oauth.userinfo.GoogleUserInfo;
import proj.chat.oauth.userinfo.KakaoUserInfo;
import proj.chat.oauth.userinfo.NaverUserInfo;
import proj.chat.oauth.userinfo.OAuth2UserInfo;
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
        
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();
        
        log.info("[loadUser] attributes={}, userNameAttributeName={}",
                attributes, userNameAttributeName);
        
        OAuth2UserInfo oAuth2UserInfo = null;
        switch (provider) {
            case "google" -> {
                log.info("[loadUser] Google 로그인");
                oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
            }
            case "kakao" -> {
                log.info("[loadUser] Kakao 로그인");
                oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
            }
            case "naver" -> {
                log.info("[loadUser] Naver 로그인");
                oAuth2UserInfo = new NaverUserInfo(
                        (Map) oAuth2User.getAttributes().get("response"));
            }
        }
        
        String providerId = Objects.requireNonNull(oAuth2UserInfo).getProviderId();
        String memberName = oAuth2UserInfo.getName();
        
        String uuid = UUID.randomUUID().toString();
        String email = oAuth2UserInfo.getEmail();
        
        log.info("[loadUser] providerId={}, memberName={}, uuid={}, email={}",
                providerId, memberName, uuid, email);
        
        Optional<Member> findMember = memberRepository.findByEmail(email);
        if (findMember.isPresent()) {
            return new CustomUserDetails(findMember.get(), oAuth2UserInfo);
        }
        
        FromSocial fromSocial = NONE;
        switch (provider) {
            case "google" -> fromSocial = GOOGLE;
            case "kakao" -> fromSocial = KAKAO;
            case "naver" -> fromSocial = NAVER;
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
        
        return new CustomUserDetails(member, oAuth2UserInfo);
    }
    
    private Member saveOrUpdate(OAuthAttributes attributes) {
        Member member = memberRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName()))
                .orElse(attributes.toEntity());
        
        return memberRepository.save(member);
    }
}
