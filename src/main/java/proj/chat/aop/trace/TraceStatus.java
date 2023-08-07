package proj.chat.aop.trace;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TraceStatus {
    
    private TraceId traceId;   // 트랜잭션 ID와 level
    private Long startTimeMs;  // 로그 시작시간
    private String message;    // 메시지
    
    @Builder
    public TraceStatus(TraceId traceId, Long startTimeMs, String message) {
        this.traceId = traceId;
        this.startTimeMs = startTimeMs;
        this.message = message;
    }
}
