package proj.chat.domain.channel.controller;

import java.util.Objects;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
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
import proj.chat.domain.channel.dto.ChannelEnterRequestDto;
import proj.chat.domain.channel.dto.ChannelMemberSearchCond;
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
    
    private static final int PAGE_SIZE = 10;
    
    /**
     * 채널 목록 조회 (+검색)
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public String list(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @ModelAttribute("channelMemberSearchCond") ChannelMemberSearchCond cond, Model model) {
        
        model.addAttribute("channels", channelService.findAll(cond.getKeyword(), page, PAGE_SIZE));
        model.addAttribute("channelSaveRequestDto", new ChannelSaveRequestDto());
        model.addAttribute("channelEnterRequestDto", new ChannelEnterRequestDto());
        return "channel/list";
    }
    
    /**
     * 채널 생성
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String create(@Validated @ModelAttribute ChannelSaveRequestDto requestDto,
            BindingResult bindingResult, Model model, @AuthenticationPrincipal User user,
            RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            
            log.info("[create] errors={}", bindingResult);
            model.addAttribute("errorMessage", "채널 생성에 실패했습니다");
            model.addAttribute("channelMemberSearchCond", new ChannelMemberSearchCond());
            model.addAttribute("channelEnterRequestDto", new ChannelEnterRequestDto());
            model.addAttribute("channels", channelService.findAll(null, 0, 10));
            
            return "channel/list";
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
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/enter")
    public String enter(
            @Validated @ModelAttribute ChannelEnterRequestDto requestDto,
            BindingResult bindingResult, Model model, @AuthenticationPrincipal User user,
            RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            
            log.info("[enter] errors={}", bindingResult);
            model.addAttribute("errorMessage", "채널 입장에 실패했습니다");
            model.addAttribute("channelSaveRequestDto", new ChannelSaveRequestDto());
            model.addAttribute("channelMemberSearchCond", new ChannelMemberSearchCond());
            model.addAttribute("channels", channelService.findAll(null, 0, 10));
            
            return "channel/list";
        }
        
        Long channelId = Long.valueOf(requestDto.getChannelId());
        String password = requestDto.getPassword();
        
        log.info("[enter] 채널 ID={}", channelId);
        log.info("[enter] 채널 비밀번호={}", requestDto.getPassword());
        
        Objects.requireNonNull(requestDto);
        Objects.requireNonNull(channelId);
        Objects.requireNonNull(password);
    
        ChannelResponseDto findChannelDto = channelService.findById(channelId);
    
        if (findChannelDto == null) {
            
            log.info("[enter] 오류로 인해 채널 입장에 실패했습니다");
            bindingResult.rejectValue(
                    "channelId", "invalid.channelId", "오류로 인해 채널 입장에 실패했습니다");
            
            model.addAttribute("errorMessage", "채널 입장에 실패했습니다");
            model.addAttribute("channelSaveRequestDto", new ChannelSaveRequestDto());
            model.addAttribute("channelMemberSearchCond", new ChannelMemberSearchCond());
            model.addAttribute("channels", channelService.findAll(null, 0, 10));
            return "channel/list";
        }
        
        if (!channelService.checkPassword(findChannelDto.getId(), password)) {
            
            log.info("[enter] 비밀번호가 일치하지 않습니다");
            bindingResult.rejectValue(
                    "password", "invalid.password", "비밀번호가 일치하지 않습니다");
            
            model.addAttribute("errorMessage", "채널 입장에 실패했습니다");
            model.addAttribute("channelSaveRequestDto", new ChannelSaveRequestDto());
            model.addAttribute("channelMemberSearchCond", new ChannelMemberSearchCond());
            model.addAttribute("channels", channelService.findAll(null, 0, 10));
            return "channel/list";
        }
        
        if (findChannelDto.getCount() >= findChannelDto.getMaxCount()) {
            
            log.info("[enterForm] 정원이 가득 찼습니다");
            redirectAttributes.addFlashAttribute("errorMessage", "정원이 가득 찼습니다");
            return "redirect:/channel/list";
        }
        
        MemberResponseDto findMemberDto = memberService.findByEmail(user.getUsername());
        
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
