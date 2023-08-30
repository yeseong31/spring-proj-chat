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
import proj.chat.domain.repository.CommentRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentService {
    
    private final CommentRepository commentRepository;
    
    @Transactional
    public Long save(CommentSaveRequestDto dto) {
    
        Comment comment = dto.dtoToEntity();
    
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
        
        commentRepository.deleteById(id);
        return id;
    }
}
