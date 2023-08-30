package proj.chat.common.config.encoder;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import org.springframework.security.crypto.password.PasswordEncoder;

public class SHA512PasswordEncoder implements PasswordEncoder {
    
    @Override
    public String encode(CharSequence rawPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("rawPassword cannot be null");
        }
        return getSHA512Password(rawPassword);
    }
    
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("rawPassword cannot be null");
        }
        if (encodedPassword == null || encodedPassword.length() == 0) {
            return false;
        }
    
        String encodedRawPw = getSHA512Password(rawPassword);
        return encodedRawPw.equals(encodedPassword);
    }
    
    private String getSHA512Password(CharSequence rawPassword) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");  // MessageDigest 클래스를 사용해서 해시 생성
            md.update(rawPassword.toString().getBytes(UTF_8));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    
        byte[] messageByte = Objects.requireNonNull(md).digest();
    
        StringBuilder sb = new StringBuilder();
        for (byte b : messageByte) {
            StringBuilder tmp = new StringBuilder(Integer.toHexString(b & 0xFF));
            while (tmp.length() < 2) {
                tmp.insert(0, "0");
            }
            sb.append(tmp.substring(tmp.length() - 2));
        }
        return sb.toString();
    }
}
