package proj.chat.repository.channel;

import static org.springframework.util.StringUtils.hasText;
import static proj.chat.domain.channel.entity.QChannel.channel;
import static proj.chat.domain.member.entity.QMember.member;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import proj.chat.entity.Channel;

@RequiredArgsConstructor
public class ChannelRepositoryImpl implements ChannelRepositoryCustom {
    
    private final JPAQueryFactory query;
    
    /**
     * 복잡한 페이징 - 데이터 조회 쿼리와 전체 카운트 쿼리를 분리
     *
     * @param keyword 검색 조건(키워드)
     * @return 채널 목록(+페이징/검색)
     */
    @Override
    public List<Channel> findSearch(String keyword) {
        
        return query
                .select(channel)
                .from(channel)
                .leftJoin(channel.owner, member)
                .where(
                        channelNameContains(keyword)
                )
                .limit(1000)
                .fetch();
    }
    
    private BooleanExpression channelNameContains(String channelName) {
        return hasText(channelName) ? channel.name.contains(channelName) : null;
    }
}
