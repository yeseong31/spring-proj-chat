package proj.chat.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import proj.chat.dto.EmailVerificationRequestDto;
import proj.chat.dto.LoginRequestDto;
import proj.chat.dto.MemberSaveRequestDto;
import proj.chat.dto.MemberUpdateRequestDto;
import proj.chat.service.AsyncMailService;
import proj.chat.service.MemberService;
import proj.chat.validator.EmailVerificationRequestDtoValidator;
import proj.chat.validator.MemberSaveRequestDtoValidator;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    
    private final MemberService memberService;
    private final AsyncMailService mailService;
    
    private final MemberSaveRequestDtoValidator memberSaveRequestDtoValidator;
    private final EmailVerificationRequestDtoValidator emailVerificationRequestDtoValidator;
    
    @InitBinder("memberSaveRequestDto")
    public void initBinder1(WebDataBinder webDataBinder) {
        // memberSaveRequestDto 객체를 받을 때 자동으로 검증이 들어감
        log.info("init binder = {}", webDataBinder);
        webDataBinder.addValidators(memberSaveRequestDtoValidator);
    }
    
    @InitBinder("emailVerificationRequestDto")
    public void initBinder2(WebDataBinder webDataBinder) {
        // emailVerificationRequestDto 객체를 받을 때 자동으로 검증이 들어감
        log.info("init binder = {}", webDataBinder);
        webDataBinder.addValidators(emailVerificationRequestDtoValidator);
    }
    
    
    /**
     * 회원가입(GET)
     */
    @GetMapping("/signup")
    public String signupForm(@ModelAttribute MemberSaveRequestDto memberSaveRequestDto) {
        return "auth/signup";
    }
    
    /**
     * 회원가입(POST)
     */
    @PostMapping("/signup")
    public String signup(
            @Validated @ModelAttribute MemberSaveRequestDto requestDto, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) throws Exception {
        
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "auth/signup";
        }
        
        // 회원가입 진행
        Long savedMemberId = memberService.save(requestDto);
        log.info("[signup] 회원가입 완료: ID={}", savedMemberId);
        
        // 이메일 인증 준비
        EmailVerificationRequestDto emailVerificationRequestDto = new EmailVerificationRequestDto();
        emailVerificationRequestDto.setEmail(requestDto.getEmail());
        
        // 이메일 인증 코드 발송 & 인증 정보 저장
        mailService.executor(requestDto.getEmail());
        
        // redirect 시 파라미터 전달
        redirectAttributes.addAttribute("email", requestDto.getEmail());
        
        return "redirect:/auth/email/verification";
    }
    
    /**
     * 로그인(GET)
     */
    @GetMapping("/login")
    public String loginForm(@ModelAttribute LoginRequestDto loginRequestDto) {
        return "auth/login";
    }
    
    /**
     * 로그아웃
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        
        return "redirect:/";
    }
    
    /**
     * 이메일 인증(GET)
     */
    @GetMapping("/email/verification")
    public String emailVerificationForm(
            @ModelAttribute EmailVerificationRequestDto emailVerificationRequestDto) {
        
        return "auth/email/verification";
    }
    
    /**
     * 이메일 인증(POST)
     */
    @PostMapping("/email/verification")
    public String emailVerification(
            @Validated @ModelAttribute EmailVerificationRequestDto requestDto,
            BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        
        log.info("[emailVerification] email={}", requestDto.getEmail());
        log.info("[emailVerification] token={}", requestDto.getToken());
        
        if (bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "auth/email/verification";
        }
        
        // 사용자 활성화
        Long findMemberId = memberService.findByEmail(requestDto.getEmail()).getId();
        
        MemberUpdateRequestDto updateDto = new MemberUpdateRequestDto();
        updateDto.setStatus(true);
        Long updatedId = memberService.update(findMemberId, updateDto);
        
        log.info("[emailVerification] 이메일 인증 성공");
        log.info("[emailVerification] 사용자 활성화 ID={}", updatedId);
        
        // redirect 시 파라미터 전달
        redirectAttributes.addFlashAttribute("message", "인증 완료! 로그인을 진행해주세요");
        
        return "redirect:/auth/login";
    }
}
