package proj.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 메시지 브로커 설정
 */
@Configuration
@EnableWebSocketMessageBroker
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    /**
     * 엔드포인트 등록
     *
     * @param registry 레지스트리
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // stomp 접속 URL: "/ws-stomp"
        registry
                .addEndpoint("/ws-stomp")
                .setAllowedOrigins("*");
    }
    
    /**
     * 메시지 브로커 설정
     *
     * @param registry 레지스트리
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메시지 구독 요청 URL -> SUBSCRIBE하는 클라이언트에게 메시지 전달
        registry.enableSimpleBroker("/sub");
        // 메시지 발행 요청 URL -> 클라이언트에서 SEND 요청 처리
        registry.setApplicationDestinationPrefixes("/pub");
    }
}
