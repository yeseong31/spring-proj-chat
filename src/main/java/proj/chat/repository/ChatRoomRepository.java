package proj.chat.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import proj.chat.dto.ChatRoomDto;

@Slf4j
@Transactional(readOnly = true)
@Repository
public class ChatRoomRepository {
    
    private final Map<String, ChatRoomDto> chatRoomMap = new ConcurrentHashMap<>();  // 임시 저장소
    
    /**
     * 채팅방 목록 조회
     *
     * @return 채팅방 목록
     */
    public List<ChatRoomDto> findAll() {
        ArrayList<ChatRoomDto> chatRoomDtos = new ArrayList<>(chatRoomMap.values());
        Collections.reverse(chatRoomDtos);  // 최근순 정렬
        return chatRoomDtos;
    }
    
    /**
     * 채팅방 조회
     *
     * @param roomId 조회할 채팅방 ID
     * @return 채팅방
     */
    public ChatRoomDto findByRoomId(String roomId) {
        return chatRoomMap.get(roomId);
    }
    
    /**
     * 채팅방 생성
     *
     * @param roomName 생성할 채팅방 이름
     * @return 채팅방 ID
     */
    @Transactional
    public String save(String roomName) {
        ChatRoomDto chatRoomDto = new ChatRoomDto(roomName);
        chatRoomMap.put(chatRoomDto.getRoomId(), chatRoomDto);
        return chatRoomDto.getRoomId();
    }
    
    /**
     * 채팅방 사용자 이름 목록 조회
     *
     * @param roomId 채팅방 ID
     * @return 사용자 이름 목록
     */
    public List<String> getMemberList(String roomId) {
        ChatRoomDto chatRoomDto = chatRoomMap.get(roomId);
        
        List<String> list = new ArrayList<>();
        chatRoomDto.getMemberList()
                .forEach((key, value) -> list.add(value));
        
        return list;
    }
}
