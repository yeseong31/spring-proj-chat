package proj.chat.aop.trace.logtrace;

import lombok.extern.slf4j.Slf4j;
import proj.chat.aop.trace.TraceId;
import proj.chat.aop.trace.TraceStatus;

@Slf4j
public class ThreadLocalLogTrace implements LogTrace {
    
    private static final String START_PREFIX = "-->";
    private static final String COMPLETE_PREFIX = "<--";
    private static final String EXCEPTION_PREFIX = "<X-";
    
    private ThreadLocal<TraceId> traceIdHolder = new ThreadLocal<>();
    
    /**
     * 로그 시작
     */
    @Override
    public TraceStatus begin(String message) {
        syncTraceId();
        TraceId traceId = traceIdHolder.get();
        Long startTimeMs = System.currentTimeMillis();
        
        log.info("[{}] {}{}",
                traceId.getId(),
                addSpace(START_PREFIX, traceId.getLevel()),
                message);
        
        return new TraceStatus(traceId, startTimeMs, message);
    }
    
    /**
     * 로그 종료
     */
    @Override
    public void end(TraceStatus status) {
        complete(status, null);
    }
    
    /**
     * 로그 종료(예외)
     */
    @Override
    public void exception(TraceStatus status, Exception e) {
        complete(status, e);
    }
    
    /**
     * 로그 출력
     */
    private void complete(TraceStatus status, Exception e) {
        Long stopTimeMs = System.currentTimeMillis();
        Long resultTimeMs = stopTimeMs - status.getStartTimeMs();
        TraceId traceId = status.getTraceId();
        
        if (e == null) {
            log.info("[{}] {}{} time={}ms", traceId.getId(),
                    addSpace(COMPLETE_PREFIX, traceId.getLevel()),
                    status.getMessage(),
                    resultTimeMs);
        } else {
            log.info("[{}] {}{} time={}ms ex={}",
                    traceId.getId(),
                    addSpace(EXCEPTION_PREFIX, traceId.getLevel()),
                    status.getMessage(),
                    resultTimeMs,
                    e.toString());
        }
        
        releaseTraceId();  // 로그 종료 시 level 감소
    }
    
    /**
     * TraceId 동기화 및 level 감소
     */
    private void syncTraceId() {
        TraceId traceId = traceIdHolder.get();
        if (traceId == null) {
            traceIdHolder.set(new TraceId());
        } else {
            traceIdHolder.set(traceId.createNextId());
        }
    }
    
    /**
     * level 감소
     */
    private void releaseTraceId() {
        TraceId traceId = traceIdHolder.get();
        if (traceId.isFirstLevel()) {
            traceIdHolder.remove();  // destroy
        } else {
            traceIdHolder.set(traceId.createPrevId());
        }
    }
    
    /**
     * 메서드 호출 깊이에 따른 공백 추가
     */
    private static String addSpace(String prefix, int level) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; i++) {
            sb.append((i == level - 1) ? "|" + prefix : "|   ");
        }
        return sb.toString();
    }
}
