package proj.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import proj.chat.domain.common.config.AopConfig;
import proj.chat.aop.trace.logtrace.LogTrace;
import proj.chat.aop.trace.logtrace.ThreadLocalLogTrace;

@Import(AopConfig.class)
@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = "proj.chat")
public class ChatApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ChatApplication.class, args);
    }
    
    @Bean
    public LogTrace logTrace() {
        return new ThreadLocalLogTrace();
    }
}
