package proj.chat.aop.trace;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TraceId {
    
    private String id;  // 트랜잭션 ID
    private int level;  // 메서드 호출 깊이
    
    public TraceId() {
        this.id = createId();
        this.level = 0;
    }
    
    private String createId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
    
    public TraceId createNextId() {
        return new TraceId(id, level + 1);
    }
    
    public TraceId createPrevId() {
        return new TraceId(id, level - 1);
    }
    
    public boolean isFirstLevel() {
        return level == 0;
    }
}
