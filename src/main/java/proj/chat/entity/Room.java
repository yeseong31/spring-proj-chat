package proj.chat.entity;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Room extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "room_id")
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Builder.Default()
    private int maxCount = 10;
    private int count;
    
    @Builder
    public Room(String name, @Nullable int maxCount) {
        this.name = name;
        this.maxCount = maxCount;
        this.count = 0;
    }
    
    // === 비즈니스 로직 ===
    public void addCount(int n) {
        if (n > 0 && count + n > maxCount) {
            throw new IllegalStateException("이미 정원이 가득 찼습니다");
        } else if (n < 0 && count + n < 0) {
            throw new IllegalStateException("아직 방에 들어온 사람이 없습니다");
        }
        this.count += n;
    }
}
