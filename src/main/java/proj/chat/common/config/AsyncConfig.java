package proj.chat.common.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    
    private static final int TASK_CORE_POOL_SIZE = 2;
    private static final int TASK_MAX_POOL_SIZE = 4;
    private static final int TASK_QUEUE_CAPACITY = 0;
    private static final String EXECUTOR_BEAN_NAME = "executor";
    
    @Bean(name="executor")
    @Override
    public Executor getAsyncExecutor() {
        
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        setExecutor(executor);
        
        return executor;
    }
    
    private void setExecutor(ThreadPoolTaskExecutor executor) {
        
        executor.setCorePoolSize(TASK_CORE_POOL_SIZE);
        executor.setMaxPoolSize(TASK_MAX_POOL_SIZE);
        executor.setQueueCapacity(TASK_QUEUE_CAPACITY);
        executor.setBeanName(EXECUTOR_BEAN_NAME);
        executor.initialize();
    }
}
