package proj.chat.domain.repository.post;

import static org.springframework.util.StringUtils.hasText;
import static proj.chat.domain.entity.QMember.member;
import static proj.chat.domain.entity.QPost.post;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import proj.chat.domain.entity.Post;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {
    
    private final JPAQueryFactory query;
    
    @Override
    public List<Post> findSearch(String keyword) {
        
        return query
                .select(post)
                .from(post)
                .leftJoin(post.member, member)
                .where(
                        postTitleContains(keyword)
                )
                .limit(1000)
                .fetch();
    }
    
    @Override
    public void addVoter(Post post1) {
    
        query
                .update(post)
                .set(post.voters, post.voters.add(1))
                .where(post.eq(post1))
                .execute();
        
    }
    
    @Override
    public void subtractVoter(Post post1) {
    
        query
                .update(post)
                .set(post.voters, post.voters.subtract(1))
                .where(post.eq(post1))
                .execute();
    }
    
    private BooleanExpression postTitleContains(String postTitle) {
        return hasText(postTitle) ? post.title.contains(postTitle) : null;
    }
}
