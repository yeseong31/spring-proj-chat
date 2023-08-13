package proj.chat.websocket.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import proj.chat.websocket.handler.WebSocketHandler;

@Slf4j
@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    
    private final WebSocketHandler webSocketHandler;
    
    /**
     * 채팅 진행을 위한 endpoint 설정: /ws/chat
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // "ws://localhost:8080/ws-chat"으로 요청이 들어오면 websocket 통신 진행
        registry
                .addHandler(webSocketHandler, "/ws-chat")
                .setAllowedOrigins("*");
    }
}