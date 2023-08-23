package proj.chat.oauth.controller;

import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import proj.chat.oauth.service.KakaoService;

@Slf4j
@Controller
@RequestMapping("/oauth/kakao")
@RequiredArgsConstructor
public class KakaoController {
    
    private final KakaoService kakaoService;
    
    @Value("${KAKAO_REST_API_KEY}")
    private String KAKAO_CLIENT_ID;
    
    @Value("${KAKAO_AUTHORIZE_URL}")
    private String KAKAO_AUTHORIZE_URL;
    
    @Value("${KAKAO_REDIRECT_URL}")
    private String KAKAO_REDIRECT_URL;
    
    @Value("${KAKAO_LOGOUT_URL}")
    private String KAKAO_LOGOUT_URL;
    
    @Value("${KAKAO_LOGOUT_REDIRECT_URL}")
    private String KAKAO_LOGOUT_REDIRECT_URL;
    
    /**
     * 카카오 계정으로 로그인하는 사용자의 정보를 얻는다.
     *
     * @param code 인가 코드
     * @param model 결과 응답에 필요한 정보를 담을 객체
     * @return TODO 로그인을 완료한 사용자는 채널 목록으로 이동
     */
    @GetMapping
    public String getClientInfo(@RequestParam String code, Model model)
            throws IOException, ParseException {
        
        log.info("[getClientInfo] code={}", code);
        
        String accessToken = kakaoService.getKakaoToken(code);
        Map<String, Object> userInfo = kakaoService.getUserInfo(accessToken);
        
        model.addAttribute("code", code);
        model.addAttribute("accessToken", accessToken);
        model.addAttribute("userInfo", userInfo);
        
        return "auth/kakao";
    }
    
    /**
     * 카카오 로그인 요청 URL로 리다이렉트한다.
     *
     * @return 카카오 로그인 요청 URL
     */
    @GetMapping("/login")
    public String kakaoLogin() {
        
        String kakaoAuthorizeUrl = KAKAO_AUTHORIZE_URL
                + "?client_id=" + KAKAO_CLIENT_ID
                + "&redirect_uri=" + KAKAO_REDIRECT_URL
                + "&response_type=code";
        
        log.info("[kakaoLogin] kakaoAuthorizeUrl={}", kakaoAuthorizeUrl);
        
        return "redirect:" + kakaoAuthorizeUrl;
    }
    
    /**
     * 카카오 계정으로 로그인한 사용자를 로그아웃 요청 URL로 리다이렉트한다.
     *
     * @return 카카오 로그아웃 요청 URL
     */
    @GetMapping("/logout")
    public String kakaoLogout() {
        
        String kakaoLogoutUrl = KAKAO_LOGOUT_URL
                + "?client_id=" + KAKAO_CLIENT_ID
                + "&logout_redirect_uri=" + KAKAO_LOGOUT_REDIRECT_URL;
    
        log.info("[kakaoLogout] kakaoLogoutUrl={}", kakaoLogoutUrl);
    
        return "redirect:" + kakaoLogoutUrl;
    }
}
