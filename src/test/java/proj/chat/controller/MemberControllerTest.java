package proj.chat.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class MemberControllerTest {
    
    @Autowired
    private MockMvc mvc;
    
    @Test
    @DisplayName("회원가입 화면 출력")
    void signupForm() throws Exception {
        mvc
                .perform(
                        get("/auth/signup")
//                                .param("name", "myName")
//                                .param("value", "myValue")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("signup"));
    }
}