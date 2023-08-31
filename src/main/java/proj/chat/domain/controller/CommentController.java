package proj.chat.domain.controller;

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
import org.springframework.web.server.ResponseStatusException;
import proj.chat.domain.dto.comment.CommentResponseDto;
import proj.chat.domain.dto.comment.CommentSaveRequestDto;
import proj.chat.domain.dto.comment.CommentUpdateRequestDto;
import proj.chat.domain.dto.member.MemberResponseDto;
import proj.chat.domain.dto.voter.VoterCommentRequestDto;
import proj.chat.domain.service.CommentService;
import proj.chat.domain.service.MemberService;
import proj.chat.domain.service.PostService;
import proj.chat.domain.service.VoterCommentService;
import proj.chat.security.auth.CustomUserDetails;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CommentController {
    
    private final CommentService commentService;
    private final PostService postService;
    private final MemberService memberService;
    private final VoterCommentService voterCommentService;
    
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/post/{postId}")
    public String save(@PathVariable("postId") Long postId,
            @Validated @ModelAttribute CommentSaveRequestDto requestDto,
            BindingResult bindingResult, Authentication authentication, Model model) {
        
        if (bindingResult.hasErrors()) {
            model.addAttribute("post", postService.findById(postId));
            return "post/detail";
        }
        
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        
        requestDto.setPostId(postId);
        Long savedId = commentService.save(requestDto, principal.getMember().getEmail());
        
        return "redirect:/post/" + postId;
    }
    
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/comment/{id}/update")
    public String updateForm(@PathVariable("id") Long id,
            @ModelAttribute("commentUpdateRequestDto") CommentUpdateRequestDto requestDto,
            Authentication authentication, Model model) {
        
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        
        CommentResponseDto findCommentDto = commentService.findById(id);
        if (!findCommentDto.getMemberEmail().equals(principal.getMember().getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다");
        }
        
        requestDto.setContent(findCommentDto.getContent());
        model.addAttribute("commentId", id);
        return "comment/update";
    }
    
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/comment/{id}/update")
    public String update(@PathVariable("id") Long id,
            @Validated @ModelAttribute CommentUpdateRequestDto requestDto,
            BindingResult bindingResult, Authentication authentication) {
        
        if (bindingResult.hasErrors()) {
            return "comment/update";
        }
        
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        
        CommentResponseDto findCommentDto = commentService.findById(id);
        if (!findCommentDto.getMemberEmail().equals(principal.getMember().getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다");
        }
        
        Long updatedId = commentService.update(id, requestDto);
        CommentResponseDto findComment = commentService.findById(updatedId);
        
        return "redirect:/post/" + findComment.getPostId();
    }
    
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/comment/{id}/delete")
    public String delete(@PathVariable("id") Long id, Authentication authentication) {
        
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        
        CommentResponseDto findCommentDto = commentService.findById(id);
        if (!findCommentDto.getMemberEmail().equals(principal.getMember().getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다");
        }
        
        Long postId = commentService.findById(id).getPostId();
        
        Long deletedId = commentService.delete(id);
        
        return "redirect:/post/" + postId;
    }
    
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/comment/{id}/vote")
    public String vote(@PathVariable("id") Long commentId, Authentication authentication) {
    
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
    
        MemberResponseDto findMemberDto = memberService.findByEmail(principal.getMember().getEmail());
        if (findMemberDto == null) {
            return "auth/signup";
        }
    
        CommentResponseDto findCommentDto = commentService.findById(commentId);
        if (findCommentDto == null) {
            return "post/list";
        }
    
        if (findCommentDto.getMemberEmail().equals(principal.getMember().getEmail())) {
            log.info("본인의 게시물은 추천할 수 없습니다");
            return "redirect:/post/" + findCommentDto.getPostId();
        }
    
        VoterCommentRequestDto requestDto = VoterCommentRequestDto.builder()
                .commentId(commentId)
                .memberId(findMemberDto.getId())
                .build();
    
        if (voterCommentService.existsPostAndMember(commentId, findMemberDto.getId())) {
            voterCommentService.delete(requestDto);
        } else {
            voterCommentService.save(requestDto);
        }
    
        return "redirect:/post/" + findCommentDto.getPostId();
    }
}
