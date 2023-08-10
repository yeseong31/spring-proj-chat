package proj.chat.handler;

import static proj.chat.entity.MessageType.ENTER;
import static proj.chat.entity.MessageType.LEAVE;
import static proj.chat.entity.MessageType.TALK;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import proj.chat.dto.PrevMessageDto;

/**
 * 웹 서버가 1대인 경우에만 정상적으로 동작하는 WebSocket 핸들러 클래스
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {
    
    // 기존 접속 사용자의 웹 소켓 세션 관리
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;
    
    /**
     * 양방향 메시지 전달
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
        PrevMessageDto chatMessage = objectMapper.readValue(payload, PrevMessageDto.class);
        chatMessage.setType(TALK);
        chatMessage.setTime(LocalDateTime.now());
        
        // 메시지를 받는 대상
        WebSocketSession receiver = sessions.get(chatMessage.getReceiver());
        
        // 메시지를 받아야 하는 타겟 상대방 조회 후 메시지 전송
        if (receiver != null && receiver.isOpen()) {
            receiver.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
        }
    }
    
    /**
     * 클라이언트 접속
     *
     * @param session 접속한 세션
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session)
            throws Exception {
        
        // 세션에 사용자 저장
        String sessionId = session.getId();
        sessions.put(sessionId, session);
        log.info("[afterConnectionEstablished] ID={} 접속", sessionId);
        
        // 입장 메시지 구성
        PrevMessageDto chatMessage = PrevMessageDto.builder()
                .type(ENTER)
                .message(sessionId + "님이 입장했습니다")
                .sender(sessionId)
                .time(LocalDateTime.now())
                .build();
        
        // 본인을 제외한 나머지 세션에 입장 이벤트 전송
        sessions.values().forEach(s -> {
            if (!s.getId().equals(sessionId)) {
                try {
                    s.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
                } catch (IOException e) {
                    log.info("[afterConnectionEstablished] errors={}", e.getMessage());
                }
            }
        });
    }
    
    /**
     * 클라이언트 접속 해제
     *
     * @param session 접속하고 있던 세션
     * @param status  상태
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status)
            throws Exception {
        
        // 세션 저장소에서 연결이 끊긴 사용자 삭제
        String sessionId = session.getId();
        sessions.remove(sessionId);
        log.info("[afterConnectionClosed] ID={} 접속 해제", sessionId);
        
        // 퇴장 메시지 구성
        PrevMessageDto chatMessage = PrevMessageDto.builder()
                .type(LEAVE)
                .message(sessionId + "님이 나갔습니다")
                .sender(sessionId)
                .time(LocalDateTime.now())
                .build();
        
        // 본인을 제외한 나머지 세션에 퇴장 이벤트 전송
        sessions.values().forEach(s -> {
            try {
                s.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
            } catch (IOException e) {
                log.info("[afterConnectionEstablished] errors={}", e.getMessage());
            }
        });
    }
    
    /**
     * 소켓 통신 에러
     *
     * @param session   접속하고 있던 세션
     * @param exception 발생한 예외
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception)
            throws Exception {
        
        log.info("[handleTransportError] errors={}", exception.getMessage());
    }
}
