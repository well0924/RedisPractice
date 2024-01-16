package com.example.redispractice.Redis;

import com.example.redispractice.vo.dto.LoginDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RedisLoginTest {

    @Autowired
    private MockMvc mvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Redis Session Test")
    public void RedisSessionLoginTest()throws Exception{
        LoginDto loginDto = new LoginDto("well4149","1234");
        mvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("RedisSession Logout Test")
    public void RedisSessionLogoutTest()throws Exception{
        mvc.perform(get("/logout")
                .contentType(MediaType.ALL))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
