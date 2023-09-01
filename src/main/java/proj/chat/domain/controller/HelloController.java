package proj.chat.domain.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class HelloController {
    
    /**
     * 홈페이지로 이동한다.
     *
     * @return 홈페이지 HTML 이름
     */
    @GetMapping({"/", "/home"})
    public String home() {
        return "home";
    }
}
