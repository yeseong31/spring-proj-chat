package proj.chat.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import proj.chat.domain.member.service.EmailTokenService;
import proj.chat.domain.member.service.MemberService;

@Slf4j
@SpringBootTest
@Transactional
class EmailTokenServiceTest {
    
    @Autowired
    EmailTokenService emailTokenService;
    
    @Autowired
    MemberService memberService;
    
}