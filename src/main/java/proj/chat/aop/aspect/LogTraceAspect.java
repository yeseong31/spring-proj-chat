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
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        TraceStatus status = null;
        
//        log.info("target={}", joinPoint.getTarget());           // 실제 호출 대상
//        log.info("getArgs={}", joinPoint.getArgs());            // 전달인자
//        log.info("getSignature={}", joinPoint.getSignature());  // join point 시그니처
        
        try {
            String message = joinPoint.getSignature().toShortString();
            status = logTrace.begin(message);
            
            // 로직 호출
            Object result = joinPoint.proceed();
            
            logTrace.end(status);
            return result;
        } catch (Exception e) {
            logTrace.exception(status, e);
            throw e;
        }
    }
}
