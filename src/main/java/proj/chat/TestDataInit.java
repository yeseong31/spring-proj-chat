package proj.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import proj.chat.domain.channel.dto.ChannelSaveRequestDto;
import proj.chat.domain.member.dto.MemberSaveRequestDto;
import proj.chat.domain.channel.service.ChannelService;
import proj.chat.domain.member.service.MemberService;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("local")
public class TestDataInit {
    
    private final MemberService memberService;
    private final ChannelService channelService;
    
    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        log.info("초기화 메서드 호출");
    
        createTestUsers();
        log.info("테스트용 사용자 데이터 저장 완료");
    
        createTestChannels();
        log.info("테스트용 채널 데이터 저장 완료");
        
        log.info("초기화 메서드 종료");
    }
    
    private void createTestUsers() {
        for (int i = 0; i < 20; i++) {
            MemberSaveRequestDto dto = MemberSaveRequestDto.builder()
                    .email("test" + i + "@test.com")
                    .name("name" + i)
                    .password("!Test" + i)
                    .matchingPassword("!Test" + i)
                    .build();
    
            memberService.save(dto);
        }
    }
    
    private void createTestChannels() {
        for (int i = 0; i < 5; i++) {
            ChannelSaveRequestDto dto = ChannelSaveRequestDto.builder()
                    .name("channel" + i)
                    .password("!Test" + i)
                    .maxCount(5)
                    .build();
            
            channelService.save(dto, "test" + i + "@test.com");
        }
    }
}
