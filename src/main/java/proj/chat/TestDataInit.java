package proj.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import proj.chat.dto.chat.ChannelSaveRequestDto;
import proj.chat.dto.member.MemberSaveRequestDto;
import proj.chat.service.ChannelService;
import proj.chat.service.MemberService;

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
    
            Long savedId = memberService.save(dto);
            log.info("회원{} 저장", savedId);
        }
        
        for (int i = 0; i < 5; i++) {
            ChannelSaveRequestDto dto = ChannelSaveRequestDto.builder()
                    .name("channel" + i)
                    .password("!Test" + i)
                    .maxCount(5)
                    .build();
    
            dto.setMemberEmail("test" + i + "@test.com");
            Long savedId = channelService.save(dto);
            log.info("채널{} 저장", savedId);
        }
        
        log.info("초기화 메서드 종료");
    }
}
