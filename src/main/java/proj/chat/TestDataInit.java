package proj.chat;

import static proj.chat.domain.entity.FromSocial.NONE;
import static proj.chat.domain.entity.MemberRole.MEMBER;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import proj.chat.domain.dto.channel.ChannelSaveRequestDto;
import proj.chat.domain.dto.member.MemberSaveRequestDto;
import proj.chat.domain.dto.post.PostSaveRequestDto;
import proj.chat.domain.service.ChannelService;
import proj.chat.domain.service.MemberService;
import proj.chat.domain.service.PostService;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("local")
public class TestDataInit {
    
    private final MemberService memberService;
    private final ChannelService channelService;
    private final PostService postService;
    
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        log.info("초기화 메서드 호출");
        
        createTestUsers();
        log.info("테스트용 사용자 데이터 저장 완료");
        
        createTestChannels();
        log.info("테스트용 채널 데이터 저장 완료");
        
        createTestPosts();
        log.info("테스트용 게시글 데이터 저장 완료");
        
        log.info("초기화 메서드 종료");
    }
    
    private void createTestUsers() {
        for (int i = 0; i < 20; i++) {
            MemberSaveRequestDto dto = MemberSaveRequestDto.builder()
                    .email("test" + i + "@test.com")
                    .name("name" + i)
                    .password("!Test" + i)
                    .matchingPassword("!Test" + i)
                    .fromSocial(NONE)
                    .role(MEMBER)
                    .build();
            
            memberService.save(dto);
        }
    }
    
    private void createTestChannels() {
        for (int i = 0; i < 11; i++) {
            ChannelSaveRequestDto dto = ChannelSaveRequestDto.builder()
                    .name("channel" + i)
                    .password("!Test" + i)
                    .maxCount(5)
                    .build();
            
            channelService.save(dto, "test" + i + "@test.com");
        }
    }
    
    private void createTestPosts() {
        
        for (int i = 0; i < 11; i++) {
            PostSaveRequestDto dto = PostSaveRequestDto.builder()
                    .title("test" + i)
                    .content("testContent" + i)
                    .build();
            
            postService.save(dto, "test" + i + "@test.com");
        }
    }
}
