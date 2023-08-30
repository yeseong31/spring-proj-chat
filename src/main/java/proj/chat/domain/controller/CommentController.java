package proj.chat.domain.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Controller
@RequiredArgsConstructor
public class CommentController {
    
    private final CommentService commentService;
    
    @PostMapping("/post/{postId}")
    public String save(@PathVariable("postId") Long postId,
            @Validated @ModelAttribute CommentSaveRequestDto requestDto,
            BindingResult bindingResult) {
    
        if (bindingResult.hasErrors()) {
        
            bindingResult.rejectValue("content", "답변 생성에 실패했습니다");
            return "post/detail";
        }
    
        Long savedId = commentService.save(requestDto);
    
        return "redirect:/post/{id}";
    }
    
    @GetMapping("/comment/{id}/update")
    public String updateForm(@PathVariable("id") Long id,
            @ModelAttribute("commentUpdateRequestDto")CommentUpdateRequestDto requestDto,
            Model model) {
    
        model.addAttribute("commentId", id);
        return "comment/update";
    }
    
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
    
    @PostMapping("/comment/{id}/delete")
    public String delete(@PathVariable("id") Long id) {
    
        Long postId = commentService.findById(id).getPostId();
    
        Long deletedId = commentService.delete(id);
    
        return "redirect:/post/" + postId;
    }
}
