package proj.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import proj.chat.domain.channel.dto.ChannelSaveRequestDto;
import proj.chat.dto.chat.MessageDto;
import proj.chat.domain.member.dto.MemberResponseDto;
import proj.chat.service.ChannelService;
import proj.chat.domain.member.service.MemberService;
import proj.chat.service.MessageService;

@Slf4j
@Controller
@RequestMapping("/channel")
@RequiredArgsConstructor
public class ChannelController {
    
    private final ChannelService channelService;
    private final MemberService memberService;
    private final MessageService messageService;
    
    /**
     * 채널 목록 조회
     */
    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("channels", channelService.findAll());
        model.addAttribute("channelSaveRequestDto", new ChannelSaveRequestDto());
        return "channel/list";
    }
    
    /**
     * 채널 생성
     */
    @PostMapping("/list")
    public String create(
            @Validated @ModelAttribute ChannelSaveRequestDto requestDto,
            BindingResult bindingResult, Model model,
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "/") String redirectUrl,
            RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            log.info("[createChannel] errors={}", bindingResult);
            model.addAttribute("channels", channelService.findAll());
            return "channel/list";
        }
        
        if (user == null) {
            log.info("[createChannel] 접근 권한이 없습니다.");
            return "redirect:" + redirectUrl;
        }
        
        // 사용자 이메일을 DTO에 저장
        requestDto.setMemberEmail(user.getUsername());
        
        log.info("[create] 채널 이름={}", requestDto.getName());
        log.info("[create] 채널 비밀번호={}", requestDto.getPassword());
        log.info("[create] 채널 최대 인원={}", requestDto.getMaxCount());
        
        String savedChannelUuid = channelService.save(requestDto);
        
        MemberResponseDto memberResponseDto = memberService.findByEmail(user.getUsername());
        
        redirectAttributes.addAttribute("name", memberResponseDto.getName());
        redirectAttributes.addAttribute("channelUuid", savedChannelUuid);
        redirectAttributes.addAttribute("status", true);
    
        return "channel/list";
//        return "redirect:/channel/{channelUuid}";
    }
    
    /**
     * 채널 입장
     */
    @GetMapping("/{channelUuid}")
    public String enter(@PathVariable String channelUuid, @ModelAttribute MessageDto messageDto) {
        return "channel/chat";
    }
}
