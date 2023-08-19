package proj.chat.domain.channel.controller;

import java.util.regex.Pattern;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import proj.chat.domain.channel.dto.ChannelMemberSearchDto;
import proj.chat.domain.channel.dto.ChannelResponseDto;
import proj.chat.domain.channel.dto.ChannelSaveRequestDto;
import proj.chat.domain.channel.service.ChannelService;
import proj.chat.domain.member.dto.MemberResponseDto;
import proj.chat.domain.member.service.MemberService;
import proj.chat.domain.message.dto.MessageDto;

@Slf4j
@Controller
@RequestMapping("/channel")
@RequiredArgsConstructor
public class ChannelController {
    
    private final ChannelService channelService;
    private final MemberService memberService;
    
    /**
     * 채널 목록 조회
     */
    @GetMapping("/list")
    public String list(
            @ModelAttribute("channelMemberSearch") ChannelMemberSearchDto searchDto,
            @AuthenticationPrincipal User user, Model model) {
        
        // 로그인하지 않은 사용자인 경우
        if (user == null) {
            log.info("[list] 로그인을 하지 않은 사용자입니다");
            return "auth/login";
        }
        
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
            model.addAttribute("errorMessage", "채널 생성에 실패했습니다");
            model.addAttribute("channels", channelService.findAll());
            return "channel/list";
        }
        
        // 로그인하지 않은 사용자인 경우
        if (user == null) {
            log.info("[create] 접근 권한이 없습니다.");
            return "redirect:" + redirectUrl;
        }
    
        // 사용자 정보를 찾지 못한 경우
        if (!memberService.existsByEmail(user.getUsername())) {
            log.info("[enterForm] 회원가입을 하지 않은 사용자입니다");
            return "redirect:/auth/signup";
        }
        
        log.info("[create] 채널 이름={}", requestDto.getName());
        log.info("[create] 채널 비밀번호={}", requestDto.getPassword());
        log.info("[create] 채널 최대 인원={}", requestDto.getMaxCount());
        
        String savedChannelUuid = channelService.save(requestDto, user.getUsername());
        
        redirectAttributes.addAttribute("uuid", savedChannelUuid);
        redirectAttributes.addAttribute("status", true);
        
        return "redirect:/channel";
    }
    
    /**
     * 채널 입장
     */
    @GetMapping
    public String enterForm(
            @RequestParam("uuid") String uuid, @AuthenticationPrincipal User user,
            Model model, RedirectAttributes redirectAttributes) {
        
        // 로그인하지 않은 사용자인 경우
        if (user == null) {
            log.info("[enterForm] 로그인을 하지 않은 사용자입니다");
            return "auth/login";
        }
        
        // 사용자 정보를 찾지 못한 경우
        MemberResponseDto findMemberDto = memberService.findByEmail(user.getUsername());
        if (findMemberDto == null) {
            log.info("[enterForm] 회원가입을 하지 않은 사용자입니다");
            return "redirect:/auth/signup";
        }
        
        // UUID 형식 확인
        if (!isMatchUuid(uuid)) {
            log.info("[enterForm] 유효하지 않은 UUID입니다");
            redirectAttributes.addFlashAttribute("errorMessage", "채널 입장 오류");
            return "redirect:/channel/list";
        }
        
        // 채널이 존재하지 않는 경우
        if (!channelService.existsByUuid(uuid)) {
            log.info("[enterForm] 유효하지 않은 UUID입니다");
            redirectAttributes.addFlashAttribute("errorMessage", "채널 입장 오류");
            return "redirect:/channel/list";
        }
        
        ChannelResponseDto findChannelDto = channelService.findByUuid(uuid);
        
        // 채널 인원이 가득 찬 경우
        if (findChannelDto.getCount() >= findChannelDto.getMaxCount()) {
            log.info("[enterForm] 정원이 가득 찼습니다");
            redirectAttributes.addFlashAttribute("errorMessage", "정원이 가득 찼습니다");
            return "redirect:/channel/list";
        }
        
        model.addAttribute("messageDto", new MessageDto());
        model.addAttribute("channelName", findChannelDto.getName());
        model.addAttribute("memberName", findMemberDto.getName());
        model.addAttribute("memberUuid", findMemberDto.getUuid());
    
        log.info("model: {}", model);
        
        return "channel/chat";
    }
    
    public static boolean isMatchUuid(String uuid) {
        Pattern regex = Pattern.compile(
                "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
        return regex.matcher(uuid).matches();
    }
}
