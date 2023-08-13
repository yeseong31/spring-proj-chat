package proj.chat.domain.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import proj.chat.aop.aspect.LogTraceAspect;
import proj.chat.aop.trace.logtrace.LogTrace;

@Configuration
public class AopConfig {
    
    @Bean
    public LogTraceAspect logTraceAspect(LogTrace logTrace) {
        return new LogTraceAspect(logTrace);
    }
}
