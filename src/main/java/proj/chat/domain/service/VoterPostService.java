package proj.chat.domain.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proj.chat.common.exception.DataNotFoundException;
import proj.chat.domain.dto.voter.VoterPostRequestDto;
import proj.chat.domain.entity.Member;
import proj.chat.domain.entity.Post;
import proj.chat.domain.entity.VoterPost;
import proj.chat.domain.repository.MemberRepository;
import proj.chat.domain.repository.VoterPostRepository;
import proj.chat.domain.repository.post.PostRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VoterPostService {
    
    private final VoterPostRepository voterPostRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    
    // TODO 중복 로직 줄이기
    
    @Transactional
    public void save(VoterPostRequestDto requestDto) {
        
        Post post = postRepository.findById(requestDto.getPostId())
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("존재하지 않는 게시글입니다 (id=%d)", requestDto.getPostId())));
    
        Member member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("존재하지 않는 사용자입니다. (id=%d)", requestDto.getMemberId())));
    
        if (voterPostRepository.existsByPostAndMember(post, member)) {
            throw new DuplicateKeyException("이미 추천을 늘렀습니다");
        }
    
        VoterPost voterPost = VoterPost.builder()
                .post(post)
                .member(member)
                .build();
        
        voterPostRepository.save(voterPost);
        
        postRepository.addVoter(post);
    }
    
    @Transactional
    public void delete(VoterPostRequestDto requestDto) {
    
        Post post = postRepository.findById(requestDto.getPostId())
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("존재하지 않는 게시글입니다 (id=%d)", requestDto.getPostId())));
    
        Member member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("존재하지 않는 사용자입니다. (id=%d)", requestDto.getMemberId())));
    
        VoterPost voterPost = voterPostRepository.findByPostAndMember(post, member)
                .orElseThrow(() -> new DataNotFoundException("추천한 기록이 없습니다"));
    
        voterPostRepository.delete(voterPost);
        
        postRepository.subtractVoter(post);
    }
    
    public boolean existsPostAndMember(Long postId, Long memberId) {
    
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            return false;
        }
    
        Optional<Member> member = memberRepository.findById(memberId);
        if (member.isEmpty()) {
            return false;
        }
    
        return voterPostRepository.existsByPostAndMember(post.get(), member.get());
    }
}
