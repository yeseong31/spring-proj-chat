package proj.chat.websocket.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    private static final int STOMP_CLIENT_PORT = 61613;
    
    @Value("${RABBITMQ_HOST}")
    private String RABBITMQ_HOST;
    
    @Value("${RABBITMQ_USERNAME}")
    private String RABBITMQ_USERNAME;
    
    @Value("${RABBITMQ_PASSWORD}")
    private String RABBITMQ_PASSWORD;
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws-rabbit")
                .setAllowedOriginPatterns("http://*.*.*.*:8081", "http://*.*.*.*:8082")
                .withSockJS();
    }
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setPathMatcher(new AntPathMatcher("."));
        
        registry.setApplicationDestinationPrefixes("/pub");
        
        // registry.enableSimpleBroker("/sub");
        registry.enableStompBrokerRelay(
                        "/queue", "/topic", "/exchange", "/amq/queue")
                .setRelayPort(STOMP_CLIENT_PORT)
                .setRelayHost(RABBITMQ_HOST)
                .setClientPasscode(RABBITMQ_PASSWORD)
                .setClientLogin(RABBITMQ_USERNAME);
    }
}
