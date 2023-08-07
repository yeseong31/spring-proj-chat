package proj.chat.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import proj.chat.entity.ChatRoom;
import proj.chat.service.ChatService;

@Slf4j
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    
    private final ChatService chatService;
    
    @GetMapping
    public List<ChatRoom> chat() {
        return chatService.findAllRooms();
    }
    
    // TODO: PostMapping으로 변경할 것
    @GetMapping("/create")
    public ChatRoom createChatRoom(@RequestParam String name) {
        return chatService.createChatRoom(name);
    }
}
