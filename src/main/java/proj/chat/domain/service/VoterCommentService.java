package proj.chat.domain.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import proj.chat.common.exception.DataNotFoundException;
import proj.chat.domain.dto.voter.VoterCommentRequestDto;
import proj.chat.domain.entity.Comment;
import proj.chat.domain.entity.Member;
import proj.chat.domain.entity.VoterComment;
import proj.chat.domain.repository.MemberRepository;
import proj.chat.domain.repository.VoterCommentRepository;
import proj.chat.domain.repository.comment.CommentRepository;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VoterCommentService {
    
    private final VoterCommentRepository voterCommentRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    
    @Transactional
    public void save(VoterCommentRequestDto requestDto) {
    
        Comment comment = commentRepository.findById(requestDto.getCommentId())
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("존재하지 않는 답변입니다 (id=%d)", requestDto.getCommentId())));
    
        Member member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("존재하지 않는 사용자입니다. (id=%d)", requestDto.getMemberId())));
    
        if (voterCommentRepository.existsByCommentAndMember(comment, member)) {
            throw new DuplicateKeyException("이미 추천을 눌렸습니다");
        }
    
        VoterComment voterComment = VoterComment.builder()
                .comment(comment)
                .member(member)
                .build();
    
        voterCommentRepository.save(voterComment);
    
        commentRepository.updateVoters(comment, true);
    }
    
    @Transactional
    public void delete(VoterCommentRequestDto requestDto) {
    
        Comment comment = commentRepository.findById(requestDto.getCommentId())
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("존재하지 않는 답변입니다 (id=%d)", requestDto.getCommentId())));
    
        Member member = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("존재하지 않는 사용자입니다. (id=%d)", requestDto.getMemberId())));
    
        VoterComment voterComment = voterCommentRepository.findByCommentAndMember(comment, member)
                .orElseThrow(() -> new DataNotFoundException("추천한 기록이 없습니다"));
        
        voterCommentRepository.delete(voterComment);
    
        commentRepository.updateVoters(comment, false);
    }
    
    public boolean existsPostAndMember(Long commentId, Long memberId) {
    
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty()) {
            return false;
        }
    
        Optional<Member> member = memberRepository.findById(memberId);
        if (member.isEmpty()) {
            return false;
        }
    
        return voterCommentRepository.existsByCommentAndMember(comment.get(), member.get());
    }
}
