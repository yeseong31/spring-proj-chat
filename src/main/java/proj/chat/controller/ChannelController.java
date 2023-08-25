package proj.chat.controller;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
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
import proj.chat.dto.channel.ChannelEnterRequestDto;
import proj.chat.dto.channel.ChannelMemberSearchCond;
import proj.chat.dto.channel.ChannelResponseDto;
import proj.chat.dto.channel.ChannelSaveRequestDto;
import proj.chat.service.ChannelService;
import proj.chat.validator.ChannelEnterRequestDtoValidator;
import proj.chat.dto.member.MemberResponseDto;
import proj.chat.service.MemberService;
import proj.chat.dto.message.MessageDto;

@Slf4j
@Controller
@RequestMapping("/channel")
@RequiredArgsConstructor
public class ChannelController {
    
    private final ChannelService channelService;
    private final MemberService memberService;
    
    private static final int PAGE_SIZE = 10;
    
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
     * @param page  조회할 페이지
     *              기본 0
     * @param cond  검색어를 포함하는 condition
     *              null 허용
     * @param model 결과 응답에 필요한 DTO 및 채널 목록을 담는 객체
     * @return 채널 목록 페이지 HTML 이름
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
     * 채널을 생성한다.
     *
     * @param requestDto    생성하고자 하는 채널 정보가 포함된 DTO
     * @param bindingResult 검증 내용에 대한 오류 내용을 보관하는 객체
     * @param model         결과 응답에 필요한 DTO 및 채널/사용자 정보를 담는 객체
     * @param user          로그인 한 사용자 정보
     *                      null이면 로그인하지 않은 사용자
     * @return 채팅 페이지 HTML 이름; 채널 생성에 실패하면 채널 목록 페이지 HTML 이름
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String create(@Validated @ModelAttribute ChannelSaveRequestDto requestDto,
            BindingResult bindingResult, Model model, @AuthenticationPrincipal User user) {
        
        if (bindingResult.hasErrors()) {
            
            controlChannelError(model, "채널 생성에 실패했습니다");
            return "channel/list";
        }
        
        log.info("[create] 채널 이름={}", requestDto.getName());
        log.info("[create] 채널 비밀번호={}", requestDto.getPassword());
        log.info("[create] 채널 최대 인원={}", requestDto.getMaxCount());
        
        String savedChannelUuid = channelService.save(requestDto, user.getUsername());
        
        MemberResponseDto findMemberDto = memberService.findByEmail(user.getUsername());
        
        model.addAttribute("messageDto", new MessageDto());
        model.addAttribute("channelName", requestDto.getName());
        model.addAttribute("channelUuid", savedChannelUuid);
        model.addAttribute("memberName", findMemberDto.getName());
        model.addAttribute("memberUuid", findMemberDto.getUuid());
        
        log.info("model: {}", model);
        
        return "channel/chat";
    }
    
    /**
     * 비밀번호를 입력하여 채널에 입장한다.
     *
     * @param requestDto         입장하고자 하는 채널의 정보가 담긴 DTO
     * @param bindingResult      검증 내용에 대한 오류 내용을 보관하는 객체
     * @param model              결과 응답에 필요한 DTO 및 채널/사용자 정보를 담는 객체
     * @param user               로그인 한 사용자 정보
     *                           null이면 로그인하지 않은 사용자
     * @param redirectAttributes 리다이렉트 응답 시 정보를 담는 객체
     * @return 채팅 페이지 HTML 이름; 채널 입장에 실패하면 채널 목록 페이지 HTML 이름
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/enter")
    public String enter(@Validated @ModelAttribute ChannelEnterRequestDto requestDto,
            BindingResult bindingResult, Model model, @AuthenticationPrincipal User user,
            RedirectAttributes redirectAttributes) {
        
        if (bindingResult.hasErrors()) {
            
            log.info("[enter] errors={}", bindingResult);
            controlChannelError(model, "채널 입장에 실패했습니다");
            
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
            
            controlChannelError(model, "채널 입장에 실패했습니다");
            bindingResult.rejectValue(
                    "channelId", "invalid.channelId", "채널 입장에 실패했습니다");
            
            return "channel/list";
        }
        
        if (!channelService.checkPassword(findChannelDto.getId(), password)) {
            
            controlChannelError(model, "비밀번호가 일치하지 않습니다.");
            bindingResult.rejectValue(
                    "password", "invalid.password", "비밀번호가 일치하지 않습니다.");
            
            return "channel/list";
        }
        
        if (findChannelDto.getCount() >= findChannelDto.getMaxCount()) {
            
            controlChannelError(model, "정원이 가득 찼습니다");
            redirectAttributes.addFlashAttribute("errorMessage", "정원이 가득 찼습니다");
            
            return "redirect:/channel/list";
        }
        
        MemberResponseDto findMemberDto = memberService.findByEmail(user.getUsername());
        
        model.addAttribute("messageDto", new MessageDto());
        model.addAttribute("channelName", findChannelDto.getName());
        model.addAttribute("channelUuid", findChannelDto.getUuid());
        model.addAttribute("memberName", findMemberDto.getName());
        model.addAttribute("memberUuid", findMemberDto.getUuid());
        
        log.info("model: {}", model);
        
        return "channel/chat";
    }
    
    /**
     * 채널 생성 및 입장 오류 시 model에 정보를 담는다.
     *
     * @param model        결과 응답에 필요한 정보를 담을 객체
     * @param errorMessage 오류 메시지
     */
    private void controlChannelError(Model model, String errorMessage) {
        
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("channelSaveRequestDto", new ChannelSaveRequestDto());
        model.addAttribute("channelMemberSearchCond", new ChannelMemberSearchCond());
        model.addAttribute("channels", channelService.findAll(null, 0, 10));
    }
}
