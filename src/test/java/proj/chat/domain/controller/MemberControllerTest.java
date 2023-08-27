package proj.chat.domain.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import proj.chat.domain.service.MemberService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MemberControllerTest {
    
    @Autowired
    MockMvc mvc;
    
    @Autowired
    MemberService memberService;
    
    
    @Test
    @DisplayName("회원가입 화면 출력")
    void signupForm() throws Exception {
        mvc.perform(get("/auth/signup"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("auth/signup"));
    }
    
    @Test
    @DisplayName("회원가입 성공")
    void signup() throws Exception {
        
        // POST 요청 수행
        mvc.perform(post("/auth/signup")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("email", "hong@test.com")
                        .param("name", "hong")
                        .param("password", "!TestHong")
                        .param("matchingPassword", "!TestHong"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/auth/email/verification"))
                .andExpect(view().name("redirect:/auth/email/verification"))
                .andDo(print());
    }
}