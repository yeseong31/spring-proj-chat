package proj.chat.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import proj.chat.dto.EmailVerificationDto;
import proj.chat.dto.LoginRequestDto;
import proj.chat.dto.MemberSaveRequestDto;
import proj.chat.service.MailService;
import proj.chat.service.MemberService;
import proj.chat.validator.MemberSaveRequestDtoValidator;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    
    private final MemberSaveRequestDtoValidator memberSaveRequestDtoValidator;
    private final MemberService memberService;
    private final MailService mailService;
    
    @InitBinder("memberSaveRequestDto")
    public void initBinder(WebDataBinder webDataBinder) {
        // memberSaveRequestDto 객체를 받을 때 자동으로 검증이 들어감
        webDataBinder.addValidators(memberSaveRequestDtoValidator);
    }
    
    /**
     * 회원가입(GET)
     */
    @GetMapping("/signup")
    public String signupForm(@ModelAttribute MemberSaveRequestDto memberSaveRequestDto) {
        return "signup";
    }
    
    /**
     * 회원가입(POST)
     */
    @PostMapping("/signup")
    public String signup(
            @Validated @ModelAttribute MemberSaveRequestDto dto, Errors errors,
            @RequestParam(defaultValue = "/") String redirectUrl, Model model) throws Exception {
        
        if (errors.hasErrors()) {
            return "signup";
        }
        
        // 회원가입 진행
        Long savedId = memberService.save(dto);
        log.info("회원가입 완료: ID={}", savedId);
    
        // 이메일 인증 준비
        EmailVerificationDto emailVerificationDto = new EmailVerificationDto();
        emailVerificationDto.setEmail(dto.getEmail());
    
        model.addAttribute("emailVerificationDto", emailVerificationDto);
        
        // 이메일 인증 코드 발송
        String token = mailService.sendSimpleMessage(dto.getEmail());
        log.info("이메일 인증 코드 발송: {}", token);
        
        // 이메일 인증 관련 내용 저장
        
        return "email-verification";
    }
    
    /**
     * 로그인(GET)
     */
    @GetMapping("/login")
    public String loginForm(@ModelAttribute LoginRequestDto loginRequestDto) {
        return "login";
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
}
