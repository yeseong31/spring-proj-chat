package proj.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import proj.chat.dto.RoomDto;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoomService {
    
    private final ObjectMapper objectMapper;
    private final HashMap<String, RoomDto> chatRooms = new HashMap<>();  // 임시 저장소
    
    /**
     * 채팅방 목록 조회
     *
     * @return 채팅방 목록
     */
    public List<RoomDto> findAllRooms() {
        return new ArrayList<>(chatRooms.values());
    }
    
    /**
     * 채팅방 조회
     *
     * @param roomId 조회하고자 하는 방 ID
     * @return 결과 채팅방
     */
    public RoomDto findRoomById(String roomId) {
        return chatRooms.get(roomId);
    }
    
    /**
     * 채팅방 생성
     *
     * @param roomName 채팅방 이름
     * @return 채팅방 ID
     */
    @Transactional
    public RoomDto createChatRoom(String roomName) {
        String roomId = UUID.randomUUID().toString().substring(0, 8);
        
        RoomDto newRoomDto = RoomDto.builder()
                .roomName(roomName)
                .build();
        
        chatRooms.put(roomId, newRoomDto);
        return newRoomDto;
    }
    
    /**
     * 지정된 세션에 메시지 전송
     *
     * @param session 메시지를 전송할 세션
     * @param message 메시지
     * @param <T>     메시지 타입
     */
    @Transactional
    public <T> void sendMessage(WebSocketSession session, T message) {
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (IOException e) {
            log.info("[sendMessage] errors={}", e.getMessage());
        }
    }
    
    /**
     * 채팅방 삭제
     *
     * @param roomId 채팅방 ID
     */
    @Transactional
    public void removeChatRoom(String roomId) {
        chatRooms.remove(roomId);
    }
}
