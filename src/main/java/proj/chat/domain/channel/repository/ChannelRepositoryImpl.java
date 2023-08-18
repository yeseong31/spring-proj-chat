package proj.chat.domain.channel.repository;

import static org.springframework.util.StringUtils.hasText;
import static proj.chat.domain.channel.entity.QChannel.channel;
import static proj.chat.domain.member.entity.QMember.member;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import proj.chat.domain.channel.dto.ChannelMemberSearchDto;
import proj.chat.domain.channel.dto.QChannelMemberSearchDto;

@RequiredArgsConstructor
public class ChannelRepositoryImpl implements ChannelRepositoryCustom {
    
    private final JPAQueryFactory query;
    
    /**
     * 복잡한 페이징 - 데이터 조회 쿼리와 전체 카운트 쿼리를 분리
     *
     * @param dto 검색 조건 DTO
     * @return 채널 목록(+페이징)
     */
    @Override
    public List<ChannelMemberSearchDto> searchChannels(ChannelMemberSearchDto dto) {
        
        return query
                .select(new QChannelMemberSearchDto(
                        member.name,
                        channel.name
                ))
                .from(channel)
                .leftJoin(channel.owner, member)
                .where(
                        memberNameEq(dto.getMemberName()),
                        channelNameEq(dto.getChannelName())
                )
                .fetch();
    }
    
    private BooleanExpression memberNameEq(String memberName) {
        return hasText(memberName) ? member.name.eq(memberName) : null;
    }
    
    private BooleanExpression channelNameEq(String channelName) {
        return hasText(channelName) ? channel.name.eq(channelName) : null;
    }
    
}
