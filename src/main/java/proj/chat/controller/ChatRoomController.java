package proj.chat.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import proj.chat.dto.ChatRoomDto;
import proj.chat.repository.ChatRoomRepository;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRoomController {
    
    private final ChatRoomRepository chatRoomRepository;
    
    /**
     * 채팅방 목록 조회
     */
    @GetMapping("/rooms")
    public String roomList(Model model) {
        List<ChatRoomDto> findAll = chatRoomRepository.findAll();
        model.addAttribute("list", findAll);
        log.info("전체 채팅방 리스트: {}", findAll);
        return "room/list";
    }
    
    /**
     * 채팅방 생성
     */
    @PostMapping("/room")
    public String createRoom(
            @RequestParam("roomName") String roomName, RedirectAttributes redirectAttributes) {
    
        String roomUUID = chatRoomRepository.save(roomName);
        log.info("채팅방 생성: UUID={}", roomUUID);
    
        redirectAttributes.addFlashAttribute("roomName", roomName);
        return "redirect:/";
    }
    
    /**
     * 채팅방 조회
     */
    @GetMapping("/room")
    public String getRoom(
            @RequestParam("roomId") String roomId, RedirectAttributes redirectAttributes) {
    
        log.info("채팅방 조회: roomId={}", roomId);
        redirectAttributes.addFlashAttribute("roomName",
                chatRoomRepository.findByRoomId(roomId).getRoomName());
        return "redirect:/chat/rooms";
    }
    
    /**
     * 채팅방 입장
     */
    @GetMapping("/join")
    public String join(String roomId, Model model) {
        log.info("roomId={}", roomId);
        model.addAttribute("room", chatRoomRepository.findByRoomId(roomId));
        return "chatroom";
    }
}
