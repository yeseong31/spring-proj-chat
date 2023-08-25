package proj.chat.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class HelloController {
    
    @GetMapping({"/", "/home"})
    public String home() {
        return "home";
    }
}
