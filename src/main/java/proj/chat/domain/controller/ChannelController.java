package proj.chat.domain.controller;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import proj.chat.domain.dto.channel.ChannelEnterRequestDto;
import proj.chat.domain.dto.channel.ChannelMemberSearchCond;
import proj.chat.domain.dto.channel.ChannelResponseDto;
import proj.chat.domain.dto.channel.ChannelSaveRequestDto;
import proj.chat.domain.dto.member.MemberResponseDto;
import proj.chat.domain.dto.message.MessageDto;
import proj.chat.domain.service.ChannelService;
import proj.chat.domain.service.MemberService;
import proj.chat.security.auth.CustomUserDetails;
import proj.chat.validator.ChannelEnterRequestDtoValidator;

@Slf4j
@Controller
@RequestMapping("/channel")
@RequiredArgsConstructor
public class ChannelController {
    
    private final ChannelService channelService;
    private final MemberService memberService;
    
    public static final int PAGE_SIZE = 10;
    
    private final ChannelEnterRequestDtoValidator channelEnterRequestDtoValidator;
    
    /**
     * 채널 입장 요청 DTO 검증을 수행한다.
     *
     * @param webDataBinder ChannelEnterRequestDto가 요청에 포함되면 이를 validator와 연결
     */
    @InitBinder("ChannelEnterRequestDto")
    public void initBinder(WebDataBinder webDataBinder) {
        log.info("init binder = {}", webDataBinder);
        webDataBinder.addValidators(channelEnterRequestDtoValidator);
    }
    
    /**
     * 검색어를 포함하여 채널 목록을 찾는다.
     *
     * @param page  조회할 페이지 기본 0
     * @param cond  검색어를 포함하는 condition null 허용
     * @param model 결과 응답에 필요한 DTO 및 채널 목록을 담는 객체
     * @return 채널 목록 페이지 HTML 이름
     */
    @GetMapping("/list")
    public String list(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @ModelAttribute("channelMemberSearchCond") ChannelMemberSearchCond cond, Model model) {
        
        model.addAttribute("channels", channelService.findAll(cond.getKeyword(), page, PAGE_SIZE));
        model.addAttribute("channelEnterRequestDto", new ChannelEnterRequestDto());
        return "channel/list";
    }
    
    /**
     * 채널 등록 페이지로 이동한다.
     *
     * @param requestDto 채널 정보를 담을 DTO
     * @return 채널 등록 패이지 HTML 이름
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/save")
    public String saveForm(@ModelAttribute ChannelSaveRequestDto requestDto) {
        
        return "channel/save";
    }
    
    /**
     * 채널을 등록한다.
     *
     * @param requestDto    등록하고자 하는 채널 정보가 포함된 DTO
     * @param bindingResult 검증 내용에 대한 오류 내용을 보관하는 객체
     * @return 채팅 페이지 HTML 이름; 채널 등록에 실패하면 채널 목록 페이지 HTML 이름
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/save")
    public String save(@Validated @ModelAttribute ChannelSaveRequestDto requestDto,
            BindingResult bindingResult, Authentication authentication) {
        
        if (bindingResult.hasErrors()) {
            return "channel/save";
        }
        
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        
        String savedChannelUuid = channelService.save(requestDto, principal.getMember().getEmail());
        
        return String.format(
                "redirect:/channel/chat?channelUuid=%s&channelName=%s",
                savedChannelUuid, requestDto.getName());
    }
    
    /**
     * 채널 입장 페이지로 이동한다.
     *
     * @param channelId  입장하고자 하는 페이지 ID(인덱스)
     * @param requestDto 입장하고자 하는 페이지 정보를 담을 DTO
     * @param model      결과 응답에 필요한 DTO 및 채널/사용자 정보를 담는 객체
     * @return 채팅방 입장 페이지 HTML
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/enter")
    public String enterForm(@RequestParam("channelId") Long channelId,
            @ModelAttribute ChannelEnterRequestDto requestDto, Model model) {
        
        ChannelResponseDto findChannelDto = channelService.findById(channelId);
        if (findChannelDto == null) {
            return "channel/list";
        }
        
        requestDto.setChannelId(findChannelDto.getId());
        model.addAttribute("channelName", findChannelDto.getName());
        return "channel/enter";
    }
    
    /**
     * 비밀번호를 입력하여 채널에 입장한다.
     *
     * @param requestDto     입장하고자 하는 채널의 정보가 담긴 DTO
     * @param bindingResult  검증 내용에 대한 오류 내용을 보관하는 객체
     * @param model          결과 응답에 필요한 DTO 및 채널/사용자 정보를 담는 객체
     * @param authentication 인증 정보
     * @return 채팅 페이지 HTML 이름; 채널 입장에 실패하면 채널 목록 페이지 HTML 이름
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/enter")
    public String enter(@Validated @ModelAttribute ChannelEnterRequestDto requestDto,
            BindingResult bindingResult, Model model, Authentication authentication) {
        
        if (bindingResult.hasErrors()) {
            return "channel/enter";
        }
        
        Long channelId = requestDto.getChannelId();
        String password = requestDto.getPassword();
        
        log.info("[enter] 채널 ID={}", channelId);
        log.info("[enter] 채널 비밀번호={}", requestDto.getPassword());
        
        Objects.requireNonNull(requestDto);
        Objects.requireNonNull(channelId);
        Objects.requireNonNull(password);
        
        ChannelResponseDto findChannelDto = channelService.findById(channelId);
        
        if (findChannelDto == null) {
            
            model.addAttribute("err", "채널 입장에 실패했습니다");
            bindingResult.rejectValue(
                    "channelId", "invalid.channelId", "채널 입장에 실패했습니다");
            
            return "channel/list";
        }
        
        if (!channelService.checkPassword(findChannelDto.getId(), password)) {
            
            model.addAttribute("err", "비밀번호가 일치하지 않습니다");
            model.addAttribute("channelName", findChannelDto.getName());
            bindingResult.rejectValue(
                    "password", "invalid.password", "비밀번호가 일치하지 않습니다");
            
            return "channel/enter";
        }
        
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        
        MemberResponseDto findMemberDto = memberService.findByEmail(
                principal.getMember().getEmail());
        
        if (findMemberDto == null) {
            
            model.addAttribute("err", "채널 입장에 실패했습니다");
            bindingResult.rejectValue(
                    "channelId", "invalid.channelId", "채널 입장에 실패했습니다");
            
            return "redirect:/auth/login";
        }
        
        model.addAttribute("messageDto", new MessageDto());
        model.addAttribute("channelName", findChannelDto.getName());
        model.addAttribute("channelUuid", findChannelDto.getUuid());
        model.addAttribute("memberName", findMemberDto.getName());
        model.addAttribute("memberUuid", findMemberDto.getUuid());
        
        log.info("model: {}", model);
        
        return "channel/chat";
    }
    
    /**
     * 채팅방 페이지로 이동한다.
     *
     * @param channelUuid    채널 UUID
     * @param channelName    채널 이름
     * @param messageDto     메시지 내용을 담을 DTO
     * @param authentication 인증 정보
     * @param model          결과 응답에 필요한 DTO 및 채널/사용자 정보를 담는 객체
     * @return 채팅방 페이지 HTML 이름
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/chat")
    public String chat(
            @RequestParam("channelUuid") String channelUuid,
            @RequestParam("channelName") String channelName,
            @ModelAttribute MessageDto messageDto, Authentication authentication, Model model) {
        
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        
        MemberResponseDto findMemberDto
                = memberService.findByEmail(principal.getMember().getEmail());
        
        model.addAttribute("memberName", findMemberDto.getName());
        model.addAttribute("memberUuid", findMemberDto.getUuid());
        model.addAttribute("channelUuid", channelUuid);
        model.addAttribute("channelName", channelName);
        return "channel/chat";
    }
}
