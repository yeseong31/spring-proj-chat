package proj.chat.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import proj.chat.dto.RoomDto;
import proj.chat.service.RoomService;

@Slf4j
@RestController
@RequestMapping("/chat/prev")
@RequiredArgsConstructor
public class ChatPrevController {
    
    private final RoomService roomService;
    
    @GetMapping
    public List<RoomDto> chat() {
        return roomService.findAllRooms();
    }
    
    // TODO: PostMapping으로 변경할 것
    @GetMapping("/create")
    public RoomDto createChatRoom(@RequestParam String name) {
        return roomService.createChatRoom(name);
    }
}
