package proj.chat.websocket.handler;

import java.util.concurrent.CountDownLatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Receiver {
    
    private final CountDownLatch latch = new CountDownLatch(1);
    
    public void receiveMessage(String message){
        log.info("<== Received: {}", message);
        latch.countDown();
    }
    
    public CountDownLatch getLatch(){
        return latch;
    }
}