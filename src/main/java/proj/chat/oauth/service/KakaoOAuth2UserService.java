package proj.chat.oauth.service;

import static proj.chat.domain.entity.FromSocial.NONE;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proj.chat.common.exception.DataNotFoundException;
import proj.chat.common.exception.OAuthTypeMatchNotFoundException;
import proj.chat.domain.entity.Member;
import proj.chat.domain.repository.MemberRepository;
import proj.chat.oauth.context.MemberContext;

/**
 * <li>loadUser() 메서드가 실행될 시점에는 이미 Kakao Access Token이 정상적으로 발급된 상태이다.</li>
 * <li>super.loadUser() 메서드를 통해 Kakao Access Token으로 User 정보를 조히한다.</li>
 */
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class KakaoOAuth2UserService extends DefaultOAuth2UserService {
    
    private final MemberRepository memberRepository;
    
    // TODO 카카오 로그인 + Spring Security
    
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        
        log.info("[loadUser] attributes={}", attributes);
        
        String oAuthId = oAuth2User.getName();
        
        Member member = null;
        String oAuthType = userRequest.getClientRegistration()
                .getRegistrationId().
                toUpperCase();
        
        if (!"KAKAO".equals(oAuthType)) {
            throw new OAuthTypeMatchNotFoundException(
                    String.format("OAuth의 타입이 \"KAKAO\"와 일치하지 않습니다: oAuthType=%s", oAuthType));
        }
        
        if (isNew(oAuthType, oAuthId)) {
            switch (oAuthType) {
                case "KAKAO" -> {
                    Map attributesProperties = (Map) attributes.get("properties");
                    Map attributesKakaoAcount = (Map) attributes.get("kakao_account");
                    String nickname = (String) attributesProperties.get("nickname");
                    
                    String email = "%s@kakao.com".formatted(oAuthId);
                    String username = "KAKAO_%s".formatted(oAuthId);
                    
                    if ((boolean) attributesKakaoAcount.get("has_email")) {
                        email = (String) attributesKakaoAcount.get("email");
                    }
                    
                    member = Member.builder()
                            .email(email)
                            .name(nickname)
                            .password("")
                            .fromSocial(NONE)
                            .status(true)
                            .build();
                    
                    memberRepository.save(member);
                }
            }
        } else {
            member = memberRepository.findByEmail("%s_%s".formatted(oAuthType, oAuthId))
                    .orElseThrow(() -> new DataNotFoundException("존재하지 않는 사용자입니다"));
        }
        
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("member"));
        return new MemberContext(member, authorities, attributes, userNameAttributeName);
    }
    
    private boolean isNew(String oAuthType, String oAuthId) {
        return memberRepository.findByEmail("%s_%s".formatted(oAuthType, oAuthId)).isEmpty();
    }
}
