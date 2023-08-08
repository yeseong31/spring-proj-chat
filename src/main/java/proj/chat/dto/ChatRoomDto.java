package proj.chat.dto;

import static lombok.AccessLevel.PROTECTED;

import java.util.HashMap;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = PROTECTED)
public class ChatRoomDto {
    
    private String roomId;    // 채팅방 ID
    private String roomName;  // 채팅방 이름
    private int count;        // 채팅방 인원
    private int maxCount;     // 채팅방 최대 인원
    
    private final HashMap<String, String> memberList = new HashMap<>();
    
    @Builder
    public ChatRoomDto(String roomName) {
        this.roomId = UUID.randomUUID().toString().substring(0, 8);
        this.roomName = roomName;
        this.count = 0;
        this.maxCount = 10;
    }
    
    /**
     * 채팅방 인원 증가
     */
    public void increaseMember() {
        if (count == maxCount) {
            throw new IllegalStateException("이미 정원이 찬 채팅방입니다");
        }
        count += 1;
    }
    
    /**
     * 채팅방 인원 감소
     */
    public void decreaseMember() {
        if (count == 0) {
            throw new IllegalStateException("빈 채팅방입니다");
        }
        count -= 1;
    }
    
    /**
     * 채팅방에 사용자 추가
     *
     * @param memberName 사용자 이름
     * @return 사용자 UUID
     */
    public String addMember(String memberName) {
        increaseMember();
        String memberUUID = UUID.randomUUID().toString().substring(0, 8);
        
        // UUID 중복 처리
        while (memberList.containsValue(memberUUID)) {
            memberUUID = UUID.randomUUID().toString().substring(0, 8);
        }
        
        memberList.put(memberUUID, memberName);
        return memberUUID;
    }
    
    /**
     * 채팅방에서 사용자 삭제
     *
     * @param memberUUID 사용자 UUID
     */
    public void deleteMember(String memberUUID) {
        decreaseMember();
        memberList.remove(memberUUID);
    }
    
    /**
     * 사용자 이름 조회
     *
     * @param memberUUID 사용자 UUID
     * @return 사용자 이름
     */
    public String getMemberName(String memberUUID) {
        return memberList.get(memberUUID);
    }
}

