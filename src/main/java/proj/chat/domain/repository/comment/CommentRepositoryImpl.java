package proj.chat.domain.repository.comment;

import static proj.chat.domain.entity.QComment.comment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import proj.chat.domain.entity.Comment;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom {
    
    private final JPAQueryFactory query;
    
    @Override
    public void updateVoters(Comment comment1, boolean b) {
    
        if (b) {
            query
                    .update(comment)
                    .set(comment.voters, comment.voters.add(1))
                    .where(comment.eq(comment1))
                    .execute();
        } else {
            query
                    .update(comment)
                    .set(comment.voters, comment.voters.subtract(1))
                    .where(comment.eq(comment1))
                    .execute();
        }
    }
}
