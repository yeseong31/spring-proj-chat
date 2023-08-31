package proj.chat.domain.controller;

import static proj.chat.domain.controller.ChannelController.PAGE_SIZE;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import proj.chat.domain.dto.comment.CommentSaveRequestDto;
import proj.chat.domain.dto.post.PostMemberSearchCond;
import proj.chat.domain.dto.post.PostSaveRequestDto;
import proj.chat.domain.dto.post.PostUpdateRequestDto;
import proj.chat.domain.service.PostService;
import proj.chat.security.auth.CustomUserDetails;

@Slf4j
@Controller
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    
    private final PostService postService;
    
    @GetMapping("/list")
    public String list(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @ModelAttribute("postMemberSearchCond") PostMemberSearchCond cond, Model model) {
        
        model.addAttribute("posts", postService.findAll(cond.getKeyword(), page, PAGE_SIZE));
        return "post/list";
    }
    
    @GetMapping
    public String saveForm(@ModelAttribute("postSaveRequestDto") PostSaveRequestDto requestDto) {
        
        return "post/save";
    }
    
    @PostMapping
    public String save(@Validated @ModelAttribute PostSaveRequestDto requestDto,
            BindingResult bindingResult, Authentication authentication) {
        
        if (bindingResult.hasErrors()) {
            
            bindingResult.rejectValue("title", "게시글 생성에 실패했습니다");
            return "post/save";
        }
    
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        
        Long savedId = postService.save(requestDto, principal.getMember().getEmail());
        
        return "redirect:/posts";
    }
    
    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        
        model.addAttribute("post", postService.findById(id));
        model.addAttribute("commentSaveRequestDto", new CommentSaveRequestDto());
        return "post/detail";
    }
    
    @GetMapping("/{id}/update")
    public String updateForm(@PathVariable("id") Long id,
            @ModelAttribute("postUpdateRequestDto") PostUpdateRequestDto requestDto,
            Model model) {
        
        model.addAttribute("postId", id);
        return "post/update";
    }
    
    @PostMapping("/{id}/update")
    public String update(@PathVariable("id") Long id,
            @Validated @ModelAttribute PostUpdateRequestDto requestDto,
            BindingResult bindingResult) {
        
        if (bindingResult.hasErrors()) {
            
            bindingResult.rejectValue("title", "게시글 수정에 실패했습니다");
            return "post/update";
        }
        
        Long updatedId = postService.update(id, requestDto);
        
        return "redirect:/post/" + id;
    }
    
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id) {
        
        Long deletedId = postService.delete(id);
        
        return "redirect:/posts";
    }
}
