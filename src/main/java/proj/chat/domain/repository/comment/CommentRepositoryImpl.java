package proj.chat.domain.repository.comment;

import static proj.chat.domain.entity.QComment.comment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import proj.chat.domain.entity.Comment;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {
    
    private final JPAQueryFactory query;
    
    @Override
    public void addVoter(Comment comment1) {
    
        query
                .update(comment)
                .set(comment.voters, comment.voters.add(1))
                .where(comment.eq(comment1))
                .execute();
    }
    
    @Override
    public void subtractVoter(Comment comment1) {
    
        query
                .update(comment)
                .set(comment.voters, comment.voters.subtract(1))
                .where(comment.eq(comment1))
                .execute();
    }
}
