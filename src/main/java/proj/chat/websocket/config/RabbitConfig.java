package proj.chat.websocket.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import proj.chat.websocket.handler.Receiver;

@Configuration
@EnableRabbit
public class RabbitConfig {
    
    public static final String CHAT_QUEUE_NAME = "chat.queue";
    public static final String CHAT_EXCHANGE_NAME = "chat.exchange";
    private static final String ROUTING_KEY = "room.*";
    
    @Value("${spring.rabbitmq.stream.host}")
    private String RABBITMQ_HOST;
    
    @Value("${spring.rabbitmq.stream.username}")
    private String RABBITMQ_USERNAME;
    
    @Value("${spring.rabbitmq.stream.password}")
    private String RABBITMQ_PASSWORD;
    
    /**
     * Queue 등록
     */
    @Bean
    public Queue queue() {
        return new Queue(CHAT_QUEUE_NAME, true);
    }
    
    /**
     * Exchange 등록
     */
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(CHAT_EXCHANGE_NAME);
    }
    
    /**
     * Exchange와 Queue 바인딩
     */
    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(ROUTING_KEY);
    }
    
    /**
     * Jackson Date
     */
    @Bean
    public com.fasterxml.jackson.databind.Module dateTimeModule() {
        return new JavaTimeModule();
    }
    
    /**
     * Spring에서 자동 생성해 주는 ConnectionFactory는 SimpleConnectionFactory이다. 여기서는
     * CachingConnectionFactory를 사용하므로 이를 등록해 준다.
     */
    @Bean
    public CachingConnectionFactory connectionFactory() {
        
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost(RABBITMQ_HOST);
        factory.setUsername(RABBITMQ_USERNAME);
        factory.setPassword(RABBITMQ_PASSWORD);
        return factory;
    }
    
    /**
     * messageConverter 커스터마이징
     */
    @Bean
    public RabbitTemplate rabbitTemplate() {
        
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        rabbitTemplate.setRoutingKey(CHAT_QUEUE_NAME);
        return rabbitTemplate;
    }
    
    @Bean
    public SimpleMessageListenerContainer container(MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setQueueNames(CHAT_QUEUE_NAME);
        container.setMessageListener(listenerAdapter);
        return container;
    }
    
    @Bean
    MessageListenerAdapter listenerAdapter(Receiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveMessage");
    }
    
    private Jackson2JsonMessageConverter jsonMessageConverter() {
        
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        objectMapper.registerModule(dateTimeModule());
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
