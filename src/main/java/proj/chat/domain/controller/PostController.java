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
    
    @GetMapping("/list")
    public String list(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @ModelAttribute("postMemberSearchCond") PostMemberSearchCond cond, Model model) {
        
        model.addAttribute("posts", postService.findAll(cond.getKeyword(), page, PAGE_SIZE));
        return "post/list";
    }
    
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/save")
    public String saveForm(@ModelAttribute("postSaveRequestDto") PostSaveRequestDto requestDto) {
        
        return "post/save";
    }
    
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
    
    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        
        model.addAttribute("post", postService.findById(id));
        model.addAttribute("commentSaveRequestDto", new CommentSaveRequestDto());
        return "post/detail";
    }
    
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
        
        return String.format("redirect:/post/%d", id);
    }
    
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id, Authentication authentication) {
    
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
    
        PostResponseDto findPostDto = postService.findById(id);
        if (!findPostDto.getMemberEmail().equals(principal.getMember().getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다");
        }
        
        Long deletedId = postService.delete(id);
        
        return "redirect:/post/list";
    }
    
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/vote")
    public String vote(@PathVariable("id") Long postId, Authentication authentication) {
    
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
    
        MemberResponseDto findMemberDto = memberService.findByEmail(principal.getMember().getEmail());
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
