package proj.chat.entity;

import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

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
    
    private int count;
    
    @Builder
    public Room(String name) {
        this.name = name;
        this.count = 0;
    }
    
    // === 비즈니스 로직 ===
    public void addCount(int n) {
        this.count += n;
    }
}
