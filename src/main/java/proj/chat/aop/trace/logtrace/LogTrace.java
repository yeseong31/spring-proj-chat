package proj.chat.aop.trace.logtrace;

import proj.chat.aop.trace.TraceStatus;

public interface LogTrace {
    
    TraceStatus begin(String message);
    
    void end(TraceStatus status);
    
    void exception(TraceStatus status, Exception e);
}
