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
    
    /**
     * 게시글에 대한 답변을 등록한다.
     *
     * @param postId         답변을 등록할 게시글
     * @param requestDto     답변 정보를 담을 DTO
     * @param bindingResult  검증 내용에 대한 오류 내용을 보관하는 객체
     * @param authentication 인증 정보
     * @param model          결과 응답에 필요한 DTO 및 채널/사용자 정보를 담는 객체
     * @return 게시글 상세 페이지 HTML 이름
     */
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
        
        Long savedId = commentService.save(requestDto, postId, principal.getMember().getEmail());
        
        return String.format("redirect:/post/%d#comment_%d", postId, savedId);
    }
    
    /**
     * 답변 수정 페이지로 이동한다.
     *
     * @param id             수정할 답변 ID(인덱스)
     * @param requestDto     답변 정보가 담긴 DTO
     * @param authentication 인증 정보
     * @param model          결과 응답에 필요한 DTO 및 채널/사용자 정보를 담는 객체
     * @return 답변 수정 페이지 HTML 이름
     */
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
    
    /**
     * 답변을 수정한다.
     *
     * @param id             수정할 답변 ID(인덱스)
     * @param requestDto     답변 정보를 담을 DTO
     * @param bindingResult  검증 내용에 대한 오류 내용을 보관하는 객체
     * @param authentication 인증 정보
     * @return 답변 수정 성공 시 게시글 상세 페이지 HTML 이름; 그렇지 않으면 답변 수정 페이지 HTML 이름
     */
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
        
        return String.format("redirect:/post/%d", findComment.getPostId());
    }
    
    /**
     * 답변을 삭제한다.
     *
     * @param id             삭제할 답변 ID(인덱스)
     * @param authentication 인증 정보
     * @return 게시글 상세 페이지 HTML 이름
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/comment/{id}/delete")
    public String delete(@PathVariable("id") Long id, Authentication authentication) {
        
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        
        CommentResponseDto findCommentDto = commentService.findById(id);
        if (!findCommentDto.getMemberEmail().equals(principal.getMember().getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다");
        }
        
        Long postId = commentService.findById(id).getPostId();
        
        commentService.delete(id);
        
        return String.format("redirect:/post/%d", postId);
    }
    
    /**
     * 답변을 추천한다.
     *
     * @param commentId      추천할 답변 ID
     * @param authentication 인증 정보
     */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/comment/{id}/vote")
    public String vote(@PathVariable("id") Long commentId, Authentication authentication) {
        
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        
        MemberResponseDto findMemberDto = memberService.findByEmail(
                principal.getMember().getEmail());
        if (findMemberDto == null) {
            return "auth/signup";
        }
        
        CommentResponseDto findCommentDto = commentService.findById(commentId);
        if (findCommentDto == null) {
            return "post/list";
        }
        
        if (findCommentDto.getMemberEmail().equals(principal.getMember().getEmail())) {
            log.info("본인의 게시물은 추천할 수 없습니다");
            return String.format("redirect:/post/%d", findCommentDto.getPostId());
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
        
        return String.format("redirect:/post/%d", findCommentDto.getPostId());
    }
}
