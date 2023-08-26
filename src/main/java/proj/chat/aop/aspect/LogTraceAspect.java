package proj.chat.aop.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import proj.chat.aop.trace.TraceStatus;
import proj.chat.aop.trace.logtrace.LogTrace;

@Slf4j
@Aspect
@RequiredArgsConstructor
public class LogTraceAspect {
    
    private final LogTrace logTrace;
    
    @Around("@annotation(proj.chat.aop.annotation.Trace)")
    public Object execute(ProceedingJoinPoint joinPoint) {
        TraceStatus status;
        
        String message = joinPoint.getSignature().toShortString();
        status = logTrace.begin(message);
        
        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            log.info("[ERROR] log trace execute error");
        }
    
        logTrace.end(status);
        return result;
    }
}
