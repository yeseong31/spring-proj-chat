package proj.chat.entity;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import proj.chat.domain.channel.entity.Channel;
import proj.chat.domain.common.entity.BaseTimeEntity;
import proj.chat.domain.member.entity.Member;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@ToString(exclude = {"member", "channel"})
public class Message extends BaseTimeEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    
    private String content;
    
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "channel_id")
    public Channel channel;
    
    @Builder
    public Message(Long id, Member member, String content, Channel channel) {
        this.id = id;
        this.member = member;
        this.content = content;
        this.channel = channel;
    }
}
