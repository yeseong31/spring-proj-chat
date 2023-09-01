package proj.chat.common.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {
    
    @Value("${spring.data.mongodb.database}")
    private String MONGODB_DATABASE_NAME;
    
    @Value("${spring.data.mongodb.host}")
    private String MONGODB_HOST;
    
    @Value("${spring.data.mongodb.port}")
    private String MONGODB_PORT;
    
    @Bean
    public MongoClient mongo() {
        
        ConnectionString connectionString = new ConnectionString(
                String.format("mongodb://%s:%s/%s",
                        MONGODB_HOST, MONGODB_PORT, MONGODB_DATABASE_NAME));
        
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        
        return MongoClients.create(mongoClientSettings);
    }
    
    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        
        return new MongoTemplate(mongo(), MONGODB_DATABASE_NAME);
    }
}
