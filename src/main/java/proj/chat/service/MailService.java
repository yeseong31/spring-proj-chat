package proj.chat.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMessage.RecipientType;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MailService {
    
    private final JavaMailSender emailSender;
    
    private String emailToken;
    
    public String sendSimpleMessage(String email) throws Exception {
        // TODO Auto-generated method stub
        emailToken = createKey();
    
        MimeMessage message = createMessage(email);
        try {
//            emailSender.send(message);
            return emailToken;
        } catch (MailException e) {
            log.error("[ERROR] {}", e.getMessage());
            throw new IllegalArgumentException();
        }
    }
    
    /**
     * 메일로 전달할 내용 생성
     * @param email 메일을 보내는 대상
     * @return 메일로 전달할 내용
     */
    private MimeMessage createMessage(String email)
            throws MessagingException, UnsupportedEncodingException {
        
        log.info("[이메일] 보내는 대상: {}", email);
        log.info("[이메일] 인증 번호: {}", emailToken);
        
        MimeMessage message = emailSender.createMimeMessage();
        message.addRecipients(RecipientType.TO, email);  // 보내는 대상
        message.setSubject("이메일 인증 테스트");             // 제목
        
        String msg = """
                    <div style='margin:20px;'>
                        <h1> 안녕하세요. </h1>
                        <br>
                        <p>아래 코드를 복사해 입력해주세요<p>
                        <br>
                        <p>감사합니다.<p>
                        <br>
                        <div align='center' style='border:1px solid black; font-family:verdana'>
                            <h3 style='color:blue;'>회원가입 인증 코드입니다.</h3>
                            <div style='font-size:130%'>
                                인증 코드: <strong>
                """;
        msg += emailToken;
        msg += """
                                </strong>
                            </div>
                            <br>
                        </div>
                    </div>
                """;
        
        message.setText(msg, "utf-8", "html");
        message.setFrom(new InternetAddress("spring.mail.username", "ys31"));
        
        return message;
    }
    
    public static String createKey() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
