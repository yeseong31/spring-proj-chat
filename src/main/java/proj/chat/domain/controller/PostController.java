package proj.chat.domain.controller;

import static proj.chat.domain.controller.ChannelController.PAGE_SIZE;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
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
import org.springframework.web.server.ResponseStatusException;
import proj.chat.domain.dto.comment.CommentSaveRequestDto;
import proj.chat.domain.dto.member.MemberResponseDto;
import proj.chat.domain.dto.post.PostMemberSearchCond;
import proj.chat.domain.dto.post.PostResponseDto;
import proj.chat.domain.dto.post.PostSaveRequestDto;
import proj.chat.domain.dto.post.PostUpdateRequestDto;
import proj.chat.domain.dto.voter.VoterPostRequestDto;
import proj.chat.domain.service.MemberService;
import proj.chat.domain.service.PostService;
import proj.chat.domain.service.VoterPostService;
import proj.chat.security.auth.CustomUserDetails;

@Slf4j
@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    
    private final PostService postService;
    private final MemberService memberService;
    private final VoterPostService voterPostService;
    
    /**
     * 게시글 목록 페이지로 이동한다.
     *
     * @param page  조회할 페이지; 기본 0
     * @param cond  검색어를 포함하는 condition; null 허용
     * @param model 결과 응답에 필요한 DTO 및 채널 목록을 담는 객체
     * @return 게시글 목록 페이지 HTML 이름
     */
    @GetMapping("/list")
    public String list(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @ModelAttribute("postMemberSearchCond") PostMemberSearchCond cond, Model model) {
        
        model.addAttribute("posts", postService.findAll(cond.getKeyword(), page, PAGE_SIZE));
        return "post/list";
    }
    
    /**
     * 게시글 등록 페이지로 이동한다.
     *
     * @param requestDto 게시글 정보를 담을 DTO
     * @return 게시글 등록 페이지 HTML 이름
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/save")
    public String saveForm(@ModelAttribute("postSaveRequestDto") PostSaveRequestDto requestDto) {
        
        return "post/save";
    }
    
    /**
     * 게시글을 등록한다.
     *
     * @param postSaveRequestDto 게시글 정보가 담긴 DTO
     * @param bindingResult      검증 내용에 대한 오류 내용을 보관하는 객체
     * @param authentication     인증 정보
     * @return 게시글 등록 성공 시 게시글 상세 페이지 HTML 이름; 그렇지 않으면 게시글 등록 페이지 HTML 이름
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/save")
    public String save(@Validated @ModelAttribute PostSaveRequestDto postSaveRequestDto,
            BindingResult bindingResult, Authentication authentication) {
        
        if (bindingResult.hasErrors()) {
            return "post/save";
        }
        
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        
        Long savedId = postService.save(postSaveRequestDto, principal.getMember().getEmail());
        
        return String.format("redirect:/post/%d", savedId);
    }
    
    /**
     * 게시글 상세 페이지로 이동한다.
     *
     * @param id    게시글 ID(인덱스)
     * @param model 결과 응답에 필요한 DTO 및 채널 목록을 담는 객체
     * @return 게시글 상세 페이지 HTML 이름
     */
    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        
        model.addAttribute("post", postService.findById(id));
        model.addAttribute("commentSaveRequestDto", new CommentSaveRequestDto());
        return "post/detail";
    }
    
    /**
     * 게시글 수정 페이지로 이동한다.
     *
     * @param id             수정할 게시글 ID(인덱스)
     * @param requestDto     게시글 정보를 담을 DTO
     * @param authentication 인증 정보
     * @param model          결과 응답에 필요한 DTO 및 채널 목록을 담는 객체
     * @return 게시글 수정 페이지 HTML 이름
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/update")
    public String updateForm(@PathVariable("id") Long id,
            @ModelAttribute("postUpdateRequestDto") PostUpdateRequestDto requestDto,
            Authentication authentication, Model model) {
        
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        
        PostResponseDto findPostDto = postService.findById(id);
        if (!findPostDto.getMemberEmail().equals(principal.getMember().getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다");
        }
        
        requestDto.setTitle(findPostDto.getTitle());
        requestDto.setContent(findPostDto.getContent());
        model.addAttribute("postId", id);
        return "post/update";
    }
    
    /**
     * 게시글을 수정한다.
     *
     * @param id             수정할 게시글 ID(인덱스)
     * @param requestDto     게시글 정보가 담긴 DTO
     * @param bindingResult  검증 내용에 대한 오류 내용을 보관하는 객체
     * @param authentication 인증 정보
     * @return 게시글 수정 성공 시 게시글 상세 페이지 HTML 이름; 그렇지 않으면 게시글 수정 페이지 HTML 이름
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/update")
    public String update(@PathVariable("id") Long id,
            @Validated @ModelAttribute PostUpdateRequestDto requestDto,
            BindingResult bindingResult, Authentication authentication) {
        
        if (bindingResult.hasErrors()) {
            return "post/update";
        }
        
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        
        PostResponseDto findPostDto = postService.findById(id);
        if (!findPostDto.getMemberEmail().equals(principal.getMember().getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다");
        }
        
        Long updatedId = postService.update(id, requestDto);
        
        return String.format("redirect:/post/%d", updatedId);
    }
    
    /**
     * 게시글을 삭제한다.
     *
     * @param id             삭제할 게시글 ID(인덱스)
     * @param authentication 인증 정보
     * @return 게시글 목록 페이지 HTML 이름
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, Authentication authentication) {
        
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        
        PostResponseDto findPostDto = postService.findById(id);
        if (!findPostDto.getMemberEmail().equals(principal.getMember().getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다");
        }
        
        postService.delete(id);
        
        return "redirect:/post/list";
    }
    
    /**
     * 게시글을 추천한다.
     *
     * @param postId         추천할 게시글 ID(인덱스)
     * @param authentication 인증 정보
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/vote")
    public String vote(@PathVariable("id") Long postId, Authentication authentication) {
        
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        
        MemberResponseDto findMemberDto = memberService.findByEmail(
                principal.getMember().getEmail());
        if (findMemberDto == null) {
            return "auth/signup";
        }
        
        PostResponseDto findPostDto = postService.findById(postId);
        if (findPostDto == null) {
            return "post/list";
        }
        
        if (findPostDto.getMemberEmail().equals(principal.getMember().getEmail())) {
            log.info("본인의 개시물은 추천할 수 없습니다");
            return String.format("redirect:/post/%d", postId);
        }
        
        VoterPostRequestDto requestDto = VoterPostRequestDto.builder()
                .postId(postId)
                .memberId(findMemberDto.getId())
                .build();
        
        if (voterPostService.existsPostAndMember(postId, findMemberDto.getId())) {
            voterPostService.delete(requestDto);
        } else {
            voterPostService.save(requestDto);
        }
        
        return String.format("redirect:/post/%d", postId);
    }
}
