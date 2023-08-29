package proj.chat.domain.controller;

import static proj.chat.domain.entity.FromSocial.NONE;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import proj.chat.domain.dto.email.EmailVerificationRequestDto;
import proj.chat.domain.dto.member.MemberLoginRequestDto;
import proj.chat.domain.dto.member.MemberResponseDto;
import proj.chat.domain.dto.member.MemberSaveRequestDto;
import proj.chat.domain.dto.member.MemberUpdateRequestDto;
import proj.chat.domain.service.EmailTokenService;
import proj.chat.domain.service.MemberService;
import proj.chat.security.service.AsyncMailService;
import proj.chat.validator.MemberSaveRequestDtoValidator;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class MemberController {
    
    private final MemberService memberService;
    private final AsyncMailService mailService;
    private final EmailTokenService emailTokenService;
    
    private final MemberSaveRequestDtoValidator memberSaveRequestDtoValidator;
    
    /**
     * 회원가입 요청 DTO 검증을 수행한다.
     *
     * @param webDataBinder MemberSaveRequestDto가 요청에 포함되면 이를 validator와 연결
     */
    @InitBinder("memberSaveRequestDto")
    public void initBinder1(WebDataBinder webDataBinder) {
        log.info("init binder = {}", webDataBinder);
        webDataBinder.addValidators(memberSaveRequestDtoValidator);
    }
    
    /**
     * 회원가입 페이지로 이동한다.
     *
     * @param memberSaveRequestDto 사용자 정보를 담을 DTO
     * @return 회원가입 페이지 HTML 이름
     */
    @GetMapping("/signup")
    public String signupForm(@ModelAttribute MemberSaveRequestDto memberSaveRequestDto) {
        return "auth/signup";
    }
    
    /**
     * 회원가입을 수행한다.
     *
     * @param requestDto    생성하고자 하는 사용자 정보가 포함된 DTO
     * @param bindingResult 검증 내용에 대한 오류 내용을 보관하는 객체
     * @return 이메일 인증 페이지 HTML 이름; 회원가입에 실패하면 회원가입 페이지 HTML 이름
     */
    @PostMapping("/signup")
    public String signup(
            @Validated @ModelAttribute MemberSaveRequestDto requestDto, BindingResult bindingResult) {
        
        if (bindingResult.hasErrors()) {
            
            log.info("[signup] errors={}", bindingResult);
            return "auth/signup";
        }
        
        try {
            Long savedId = memberService.save(requestDto);
            log.info("[signup] 회원가입 완료: ID={}", savedId);
            
            mailService.executor(requestDto.getEmail());
            
            MemberResponseDto findMember = memberService.findById(savedId);
            
            return "redirect:/auth/email/verification/" + findMember.getUuid();
        } catch (DataIntegrityViolationException e) {
            bindingResult.rejectValue("email", "duplicate.email",
                    "이미 등록된 사용자입니다");
            return "auth/signup";
        } catch (MessagingException | UnsupportedEncodingException e) {
            bindingResult.reject("signupFailed", "회원가입에 실패했습니다");
            return "auth/signup";
        }
    }
    
    /**
     * 로그인 페이지로 이동한다.
     *
     * @param loginRequestDto 로그인 정보를 담을 DTO
     * @return 로그인 페이지 HTML 이름
     */
    @GetMapping("/login")
    public String loginForm(
            @ModelAttribute("loginRequestDto") MemberLoginRequestDto loginRequestDto) {
        
        return "auth/login";
    }
    
    /**
     * 로그아웃을 진행한다.
     *
     * @param request  하나의 HTTP 요청 정보를 담는 객체
     * @param response 하나의 HTTP 응답 정보를 담는 객체
     * @return 홈페이지 HTML 이름
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
     * 이메일 인증 페이지로 이동한다.
     *
     * @param model   결과 응답에 필요한 DTO 및 채널 목록을 담는 객체
     * @return 이메일 인증 페이지 HTML 이름; 이메일 인증 페이지 이동 실패 시 회원가입 페이지 HTML 이름
     */
    @GetMapping("/email/verification/{sessionMemberUuid}")
    public String emailVerificationForm(Model model,
            @PathVariable("sessionMemberUuid") String sessionMemberUuid) {
        
        if (sessionMemberUuid == null) {
            
            log.info("[emailVerificationForm] 회원가입을 하지 않은 사용자입니다");
            model.addAttribute("memberSaveRequestDto", new MemberSaveRequestDto());
            return "auth/signup";
        }
        
        String findMemberUuid = memberService.findByUuid(sessionMemberUuid).getUuid();
        
        if (!findMemberUuid.equals(sessionMemberUuid)) {
            
            log.info("[emailVerificationForm] 올바르지 않은 사용자입니다");
            model.addAttribute("memberSaveRequestDto", new MemberSaveRequestDto());
            return "auth/signup";
        }
        
        model.addAttribute("emailVerificationRequestDto", new EmailVerificationRequestDto());
        
        return "auth/email/verification";
    }
    
    /**
     * 이메일 인증 코드를 입력받아 사용자 인증을 완료한다.
     *
     * @param requestDto         이메일 인증 정보가 포함된 DTO
     * @param bindingResult      검증 내용에 대한 오류 내용을 보관하는 객체
     * @param redirectAttributes 리다이렉트 응답 시 정보를 담는 객체
     * @param request            하나의 HTTP 요청 정보를 담는 객체
     * @return 로그인 페이지 HTML 이름; 이메일 인증 실패 시 이메일 인증 페이지 HTML 이름
     */
    @PostMapping("/email/verification/{sessionMemberUuid}")
    public String emailVerification(
            @PathVariable("sessionMemberUuid") String sessionMemberUuid,
            @Validated @ModelAttribute EmailVerificationRequestDto requestDto,
            BindingResult bindingResult, RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        
        if (bindingResult.hasErrors()) {
            
            log.info("[emailVerification] errors={}", bindingResult);
            return "auth/email/verification";
        }
        
        Objects.requireNonNull(sessionMemberUuid);
        
        MemberResponseDto findMember = memberService.findByUuid(sessionMemberUuid);
        String findMemberUuid = findMember.getUuid();
        
        if (!findMemberUuid.equals(sessionMemberUuid)) {
            
            log.info("[emailVerificationForm] 올바르지 않은 사용자입니다");
            bindingResult.rejectValue("email", "invalid.email",
                    "올바르지 않은 사용자입니다");
            return "auth/signup";
        }
        
        if (!emailTokenService.checkToken(findMember.getId(), requestDto.getToken())) {
            
            log.info("[emailVerificationForm] 인증 번호가 일치하지 않습니다");
            bindingResult.rejectValue(
                    "token", "invalid.token", "인증 번호가 일치하지 않습니다");
            return "auth/email/verification";
        }
        
        MemberUpdateRequestDto updateDto = MemberUpdateRequestDto.builder()
                .email(findMember.getEmail())
                .fromSocial(NONE)
                .status(true)
                .build();
        
        Long updatedId = memberService.update(findMember.getId(), updateDto);
        
        log.info("[emailVerification] 이메일 인증 성공");
        log.info("[emailVerification] 사용자 활성화: ID={}", updatedId);
        
        redirectAttributes.addFlashAttribute("message", "인증 완료! 로그인을 진행해주세요");
        
        return "redirect:/auth/login";
    }
}
