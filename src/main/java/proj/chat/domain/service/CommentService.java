package proj.chat.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proj.chat.common.exception.DataNotFoundException;
import proj.chat.domain.dto.comment.CommentResponseDto;
import proj.chat.domain.dto.comment.CommentSaveRequestDto;
import proj.chat.domain.dto.comment.CommentUpdateRequestDto;
import proj.chat.domain.entity.Comment;
import proj.chat.domain.entity.Member;
import proj.chat.domain.entity.Post;
import proj.chat.domain.repository.CommentRepository;
import proj.chat.domain.repository.MemberRepository;
import proj.chat.domain.repository.post.PostRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {
    
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    
    @Transactional
    public Long save(CommentSaveRequestDto dto, String memberEmail) {
    
        Post post = postRepository.findById(dto.getPostId())
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("존재하지 않는 게시글입니다 (id=%d)", dto.getPostId())));
        
        dto.setPost(post);
    
        Member findMember = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new DataNotFoundException("존재하지 않는 사용자입니다"));
        
        Comment comment = dto.dtoToEntity();
        
        comment.addComment();
        comment.registerMember(findMember);
    
        return commentRepository.save(comment).getId();
    }
    
    public CommentResponseDto findById(Long id) {
    
        Comment findComment = commentRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("존재하지 않는 답변입니다 (id=%d)", id)));
    
        return new CommentResponseDto(findComment);
    }
    
    @Transactional
    public Long update(Long id, CommentUpdateRequestDto dto) {
        
        Comment findComment = commentRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("존재하지 않는 답변입니다 (id=%d)", id)));
    
        findComment.update(dto.getContent());
        return id;
    }
    
    @Transactional
    public Long delete(Long id) {
    
        Comment findComment = commentRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("존재하지 않는 답변입니다 (id=%d)", id)));
    
        findComment.removeComment();
        
        commentRepository.deleteById(id);
        return id;
    }
}
