package proj.chat.domain.common.config;

import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;

@Configuration
public class AuditConfig implements AuditorAware<String> {
    
    // TODO: createdBy, lastModifiedBy 값을 UUID가 아닌 사용자 값으로 바꾸어야 함
    
    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(UUID.randomUUID().toString());
    }
}
