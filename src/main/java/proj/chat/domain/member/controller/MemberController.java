package proj.chat.domain.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import proj.chat.domain.member.dto.EmailVerificationRequestDto;
import proj.chat.domain.member.dto.MemberLoginRequestDto;
import proj.chat.domain.member.dto.MemberResponseDto;
import proj.chat.domain.member.dto.MemberSaveRequestDto;
import proj.chat.domain.member.dto.MemberUpdateRequestDto;
import proj.chat.domain.member.service.EmailTokenService;
import proj.chat.domain.member.service.MemberService;
import proj.chat.domain.member.validator.MemberSaveRequestDtoValidator;
import proj.chat.security.service.AsyncMailService;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class MemberController {
    
    private final MemberService memberService;
    private final AsyncMailService mailService;
    private final EmailTokenService emailTokenService;
    
    private final MemberSaveRequestDtoValidator memberSaveRequestDtoValidator;
    
    // TODO: WebInitBinder를 적용한 뒤로 검증에 대한 테스트를 진행할 수 없는 문제가 있음
    
    @InitBinder("memberSaveRequestDto")
    public void initBinder1(WebDataBinder webDataBinder) {
        // memberSaveRequestDto 객체를 받을 때 자동으로 검증이 들어감
        log.info("init binder = {}", webDataBinder);
        webDataBinder.addValidators(memberSaveRequestDtoValidator);
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
            HttpServletRequest request) throws Exception {
        
        if (bindingResult.hasErrors()) {
            log.info("[signup] errors={}", bindingResult);
            return "auth/signup";
        }
        
        // 회원가입 진행
        Long savedId = memberService.save(requestDto);
        log.info("[signup] 회원가입 완료: ID={}", savedId);
        
        // 이메일 인증 코드 발송 & 인증 정보 저장
        mailService.executor(requestDto.getEmail());
        
        MemberResponseDto findMember = memberService.findById(savedId);
        
        // 세션에 사용자 UUID 저장
        HttpSession session = request.getSession();
        session.setAttribute("uuid", findMember.getUuid());
        
        return "redirect:/auth/email/verification";
    }
    
    /**
     * 로그인(GET)
     */
    @GetMapping("/login")
    public String loginForm(
            @ModelAttribute("loginRequestDto") MemberLoginRequestDto loginRequestDto) {
        
        return "auth/login";
    }
    
    /**
     * 로그아웃
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null) {
            log.info("[logout] 로그아웃: email={}", authentication.getName());
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        
        return "redirect:/";
    }
    
    /**
     * 이메일 인증(GET)
     */
    @GetMapping("/email/verification")
    public String emailVerificationForm(Model model, HttpServletRequest request) {
        
        HttpSession session = request.getSession();
        String memberUuid = (String) session.getAttribute("uuid");
        
        if (memberUuid == null) {
            log.info("[emailVerificationForm] 회원가입을 하지 않은 사용자입니다");
            model.addAttribute("memberSaveRequestDto", new MemberSaveRequestDto());
            return "auth/signup";
        }
        
        if (!memberService.findByUuid(memberUuid).getUuid().equals(memberUuid)) {
            log.info("[emailVerificationForm] 올바르지 않은 사용자입니다");
            model.addAttribute("memberSaveRequestDto", new MemberSaveRequestDto());
            return "auth/signup";
        }
        
        model.addAttribute("emailVerificationRequestDto", new EmailVerificationRequestDto());
        
        return "auth/email/verification";
    }
    
    /**
     * 이메일 인증(POST)
     */
    @PostMapping("/email/verification")
    public String emailVerification(
            @Validated @ModelAttribute EmailVerificationRequestDto requestDto,
            BindingResult bindingResult, RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        
        if (bindingResult.hasErrors()) {
            log.info("[emailVerification] errors={}", bindingResult);
            return "auth/email/verification";
        }
        
        // 세션에 저장되어 있는 사용자 UUID 확인
        HttpSession session = request.getSession();
        String memberUuid = (String) session.getAttribute("uuid");
        
        Objects.requireNonNull(memberUuid);
        
        MemberResponseDto findMember = memberService.findByUuid(memberUuid);
        
        // 세션의 UUID가 사용자 UUID와 다른 경우
        if (!findMember.getUuid().equals(memberUuid)) {
            log.info("[emailVerificationForm] 올바르지 않은 사용자입니다");
            bindingResult.rejectValue("email", "invalid.email",
                    "올바르지 않은 사용자입니다");
            return "auth/signup";
        }
        
        // 인증 정보가 생성되지 않은 경우
        if (!emailTokenService.checkToken(findMember.getId(), requestDto.getToken())) {
            log.info("[emailVerificationForm] 인증 번호가 일치하지 않습니다");
            bindingResult.rejectValue("token", "invalid.token",
                    "인증 번호가 일치하지 않습니다");
            return "auth/email/verification";
        }
        
        // 사용자 활성화 및 저장
        MemberUpdateRequestDto updateDto = MemberUpdateRequestDto.builder()
                .email(findMember.getEmail())
                .status(true)
                .build();
        
        Long updatedId = memberService.update(findMember.getId(), updateDto);
        
        log.info("[emailVerification] 이메일 인증 성공");
        log.info("[emailVerification] 사용자 활성화: ID={}", updatedId);
        
        // 세션에 저장되어 있는 사용자 UUID 삭제
        session.removeAttribute("uuid");
        
        // redirect 시 파라미터 전달
        redirectAttributes.addFlashAttribute("message", "인증 완료! 로그인을 진행해주세요");
        
        return "redirect:/auth/login";
    }
}
