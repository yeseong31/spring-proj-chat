package proj.chat.oauth.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import proj.chat.domain.dto.member.MemberSaveRequestDto;

@Slf4j
@Service
public class KakaoService {
    
    @Value("${KAKAO_REST_API_KEY}")
    private String KAKAO_CLIENT_ID;
    
    @Value("${KAKAO_REDIRECT_URL}")
    private String KAKAO_REDIRECT_URL;
    
    @Value("${KAKAO_RENEW_TOKEN_URL}")
    private String KAKAO_RENEW_TOKEN_URL;
    
    @Value("${KAKAO_GET_USER_INFO_URL}")
    private String KAKAO_GET_USER_INFO_URL;
    
    @Value("${KAKAO_GET_AGREEMENT_INFO_URL}")
    private String KAKAO_GET_AGREEMENT_INFO_URL;
    
    /**
     * 카카오 로그인에 필요한 액세스 토큰을 얻는다.
     *
     * @param code 인가 코드
     * @return 액세스 토큰
     */
    public String getKakaoToken(String code) throws IOException, ParseException {
        
        String accessToken;
        
        HttpURLConnection conn = getConnection(KAKAO_RENEW_TOKEN_URL);
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        
        String targetUrl = createKakaoRequestUrl(code);
        
        writeBufferedURL(conn, targetUrl);
        
        int responseCode = conn.getResponseCode();
        log.info("[getKakaoToken] responseCode={}", responseCode);
        
        String result = readBuffer(conn);
        log.info("[getKakaoToken] result={}", result);
        
        JSONObject obj = getJsonObject(result);
        accessToken = obj.get("access_token").toString();
        log.info("[getKakaoToken] accessToken={}", accessToken);
        
        return accessToken;
    }
    
    /**
     * 카카오 로그인을 진행한 사용자의 정보를 가져온다.
     *
     * @param accessToken 액세스 토큰
     * @return 사용자 정보; ID(인덱스), 이름, 이메일
     */
    public MemberSaveRequestDto getUserInfo(String accessToken) throws IOException, ParseException {
        
        Map<String, Object> resultMap = new ConcurrentHashMap<>();
        
        HttpURLConnection conn = getConnection(KAKAO_GET_USER_INFO_URL);
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        conn.setRequestMethod("GET");
        
        int responseCode = conn.getResponseCode();
        log.info("[getUserInfo] responseCode={}", responseCode);
        
        String result = readBuffer(conn);
        log.info("[getUserInfo] result={}", result);
        
        JSONObject obj = getJsonObject(result);
        JSONObject properties = (JSONObject) obj.get("properties");
        JSONObject kakaoAccount = (JSONObject) obj.get("kakao_account");
        
        String id = obj.get("id").toString();
        String nickname = properties.get("nickname").toString();
        String email = kakaoAccount.get("email").toString();
        
        resultMap.put("id", id);
        resultMap.put("nickname", nickname);
        resultMap.put("email", email);
        
        log.info("[getUserInfo] resultMap={}", resultMap);
    
        return MemberSaveRequestDto.builder()
                .email(email)
                .name(nickname)
                .build();
    }
    
    /**
     * 카카오 로그인 시 동의 내역을 확인한다.
     *
     * @param accessToken 액세스 토큰
     * @return 동의 내역 정보
     */
    public String getAgreementInfo(String accessToken) throws IOException {
        
        String result;
        
        HttpURLConnection conn = getConnection(KAKAO_GET_AGREEMENT_INFO_URL);
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        conn.setRequestMethod("GET");
        
        result = readBuffer(conn);
        int responseCode = conn.getResponseCode();
        log.info("[getAgreementInfo] responseCode={}", responseCode);
        
        return result;
    }
    
    /**
     * 커넥션을 호스트와 연결한다.
     *
     * @param host 호스트
     * @return 호스트와 연결된 커넥션
     */
    private HttpURLConnection getConnection(String host) throws IOException {
        
        URL url = new URL(host);
        return (HttpURLConnection) url.openConnection();
    }
    
    /**
     * 카카오 로그인 서비스의 인가 코드를 받을 수 있는 URL을 생성한다.
     *
     * @param code 인가 코드
     * @return 카카오 로그인 서비스의 인가 코드를 받을 수 있는 URL
     */
    private String createKakaoRequestUrl(String code) {
        
        return "grant_type=authorization_code"
                + "&client_id=" + KAKAO_CLIENT_ID
                + "&redirect_uri=" + KAKAO_REDIRECT_URL
                + "&code=" + code;
    }
    
    /**
     * 버퍼에 URL 정보를 쓴다.
     *
     * @param conn 호스트와 연결된 커넥션
     * @param url 대상 URL
     */
    private void writeBufferedURL(HttpURLConnection conn, String url) throws IOException {
        
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()))) {
            bw.write(url);
            bw.flush();
        }
    }
    
    /**
     * 버퍼에 적힌 내용을 읽는다.
     *
     * @param conn 호스트와 연결된 커넥셭
     * @return 버퍼에서 읽은 내용
     */
    private String readBuffer(HttpURLConnection conn) throws IOException {
        
        String line;
        StringBuilder result = new StringBuilder();
        
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            while ((line = br.readLine()) != null) {
                result.append(line);
            }
        }
        
        return String.valueOf(result);
    }
    
    /**
     * JSON 문자열을 파싱한다.
     *
     * @param result 파싱하고자 하는 JSON 데이터
     * @return 파싱한 결과 문자열
     */
    private JSONObject getJsonObject(String result) throws ParseException {
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(result);
    }
}
