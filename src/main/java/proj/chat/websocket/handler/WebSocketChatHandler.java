package proj.chat.websocket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import proj.chat.dto.ChatMessageDto;
import proj.chat.dto.RoomDto;
import proj.chat.service.RoomService;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketChatHandler extends TextWebSocketHandler {
    
    private final ObjectMapper objectMapper;
    private final RoomService roomService;
    
    /**
     * 메시지 전달
     *
     * @param session 대상이 되는 세션
     * @param message 메시지
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message)
            throws Exception {
        
        // 웹 소켓 클라이언트로부터 채팅 메시지(JSON)를 전달받음
        String payload = message.getPayload();
        log.info("[handleTextMessage] payload={}", payload);
        
        // 전달받은 채팅 메시지를 채팅 메시지 객체로 변환 (JSON -> ChatDto)
        ChatMessageDto chatMessage = objectMapper.readValue(payload, ChatMessageDto.class);
        log.info("[handleTextMessage] session={}", chatMessage.toString());
        
        // 전달받은 채팅 메시지에 담겨 있는 채팅방 조회
        RoomDto targetRoomDto = roomService.findRoomById(chatMessage.getRoomId());
        log.info("[handleTextMessage] room={}", targetRoomDto.toString());
        
        // 해당 채팅방에 입장해 있는 모든 Websocket Session을 대상으로 타입에 따른 메시지 전달
        targetRoomDto.handlerActions(session, chatMessage, roomService);
    }
    
    /**
     * 클라이언트 접속 시 호출
     *
     * @param session 접속한 세션
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("[afterConnectionEstablished] {} 클라이언트 접속", session);
    }
    
    /**
     * 클라이언트 접속 해제 시 호출
     *
     * @param session 접속하고 있던 세션
     * @param status 상태
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status)
            throws Exception {
        
        log.info("[afterConnectionClosed] {} 클라이언트 접속 해제", session);
    }
}
