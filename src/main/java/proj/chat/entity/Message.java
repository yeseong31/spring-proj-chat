package proj.chat.entity;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@ToString(exclude = {"member", "room"})
public class Message {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "room_id")
    private Room room;
    
    @Lob
    @Column(columnDefinition = "BLOB")
    private String text;
    
    // === 비즈니스 로직 ===
    
    /**
     * 방 입장 시 정원 수 갱신
     */
    public void enter() {
        getRoom().addCount(-1);
    }
    
    /**
     * 방 퇴장 시 정원 수 갱신
     */
    public void leave() {
        getRoom().addCount(1);
    }
}
