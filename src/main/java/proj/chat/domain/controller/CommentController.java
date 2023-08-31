package proj.chat.domain.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import proj.chat.domain.dto.comment.CommentResponseDto;
import proj.chat.domain.dto.comment.CommentSaveRequestDto;
import proj.chat.domain.dto.comment.CommentUpdateRequestDto;
import proj.chat.domain.service.CommentService;
import proj.chat.domain.service.PostService;
import proj.chat.security.auth.CustomUserDetails;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CommentController {
    
    private final PostService postService;
    private final CommentService commentService;
    
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
            Model model) {
    
        model.addAttribute("commentId", id);
        return "comment/update";
    }
    
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/comment/{id}/update")
    public String update(@PathVariable("id") Long id,
            @Validated @ModelAttribute CommentUpdateRequestDto requestDto,
            BindingResult bindingResult) {
    
        if (bindingResult.hasErrors()) {
            
            bindingResult.rejectValue("content", "답변 수정에 실패했습니다");
            return "comment/update";
        }
    
        Long updatedId = commentService.update(id, requestDto);
        CommentResponseDto findComment = commentService.findById(updatedId);
    
        return "redirect:/post/" + findComment.getPostId();
    }
    
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/comment/{id}/delete")
    public String delete(@PathVariable("id") Long id) {
    
        Long postId = commentService.findById(id).getPostId();
    
        Long deletedId = commentService.delete(id);
    
        return "redirect:/post/" + postId;
    }
}
