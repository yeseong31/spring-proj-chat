package proj.chat.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proj.chat.common.exception.DataNotFoundException;
import proj.chat.domain.dto.post.PostResponseDto;
import proj.chat.domain.dto.post.PostSaveRequestDto;
import proj.chat.domain.dto.post.PostUpdateRequestDto;
import proj.chat.domain.entity.Post;
import proj.chat.domain.repository.PostRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {
    
    private final PostRepository postRepository;
    
    @Transactional
    public Long save(PostSaveRequestDto dto) {
    
        Post post = dto.dtoToEntity();
    
        return postRepository.save(post).getId();
    }
    
    public PostResponseDto findById(Long id) {
    
        Post findPost = postRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("존재하지 않는 게시글입니다 (id=%d)", id)));
    
        return new PostResponseDto(findPost);
    }
    
    @Transactional
    public Long update(Long id, PostUpdateRequestDto dto) {
    
        Post findPost = postRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("존재하지 않는 게시글입니다 (id=%d)", id)));
    
        findPost.update(dto.getTitle(), dto.getContent());
        return id;
    }
    
    @Transactional
    public Long delete(Long id) {
        
        postRepository.deleteById(id);
        return id;
    }
}
