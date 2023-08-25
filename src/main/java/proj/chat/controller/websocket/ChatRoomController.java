package proj.chat.controller.websocket;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/chat")
public class ChatRoomController {
    
    @GetMapping("/rooms")
    public String getRooms() {
        return "chat/rooms";
    }
    
    @GetMapping(value = "/room")
    public String getRoom(Long channelUuid, String memberName, String memberUuid, Model model){
        
        model.addAttribute("channelUuid", channelUuid);
        model.addAttribute("memberName", memberName);
        model.addAttribute("memberUuid", memberUuid);
        
        return "chat/room";
    }
}
