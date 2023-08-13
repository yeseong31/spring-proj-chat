package proj.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import proj.chat.dto.chat.ChannelSaveRequestDto;
import proj.chat.domain.member.dto.MemberSaveRequestDto;
import proj.chat.service.ChannelService;
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
    
        for (int i = 0; i < 20; i++) {
            MemberSaveRequestDto dto = MemberSaveRequestDto.builder()
                    .email("test" + i + "@test.com")
                    .name("name" + i)
                    .password("!Test" + i)
                    .matchingPassword("!Test" + i)
                    .build();
    
            memberService.save(dto);
        }
    
        log.info("테스트용 사용자 데이터 저장 완료");
        
        for (int i = 0; i < 5; i++) {
            ChannelSaveRequestDto dto = ChannelSaveRequestDto.builder()
                    .name("channel" + i)
                    .password("!Test" + i)
                    .maxCount(5)
                    .build();
    
            dto.setMemberEmail("test" + i + "@test.com");
            channelService.save(dto);
        }
    
        log.info("테스트용 채널 데이터 저장 완료");
        log.info("초기화 메서드 종료");
    }
}
