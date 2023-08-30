package proj.chat.domain.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proj.chat.common.exception.DataNotFoundException;
import proj.chat.domain.dto.post.PostResponseDto;
import proj.chat.domain.dto.post.PostSaveRequestDto;
import proj.chat.domain.dto.post.PostUpdateRequestDto;
import proj.chat.domain.entity.Post;
import proj.chat.domain.repository.post.PostRepository;

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
    
    public Page<PostResponseDto> findAll(String keyword, int page, int size) {
    
        Pageable pageable = PageRequest.of(page, size);
        
        List<PostResponseDto> result = postRepository.findSearch(keyword).stream()
                .map(PostResponseDto::new)
                .sorted(Comparator.comparing(PostResponseDto::getCreatedDate).reversed())
                .collect(Collectors.toList());
    
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), result.size());
    
        List<PostResponseDto> pageContent = result.subList(start, end);
    
        return new PageImpl<>(pageContent, pageable, result.size());
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
    
        postRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("존재하지 않는 게시글입니다 (id=%d)", id)));
        
        postRepository.deleteById(id);
        return id;
    }
}
