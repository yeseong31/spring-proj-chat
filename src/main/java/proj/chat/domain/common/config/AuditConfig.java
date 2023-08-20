package proj.chat.domain.common.config;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@Configuration
public class AuditConfig implements AuditorAware<String> {
    
    @Override
    public Optional<String> getCurrentAuditor() {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            log.info("[getCurrentAuditor] 사용자 정보를 찾을 수 없습니다");
            return Optional.empty();
        }
        
        String authenticationName = authentication.getName();
        log.info("[getCurrentAuditor] 사용자 정보={}", authenticationName);
        
        return Optional.of(authenticationName);
    }
}
