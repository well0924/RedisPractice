package com.example.redispractice.controller;

import com.example.redispractice.vo.dto.LoginDto;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@AllArgsConstructor
public class TestController {

    @PostMapping("/login")
    ResponseEntity<?>login(@RequestBody LoginDto loginDto, HttpSession session){
        session.setAttribute("loginUser",loginDto);
        return ResponseEntity.ok("Login O.K");
    }

    @GetMapping("/logout")
    public ResponseEntity<?>logout(HttpSession session){
        session.removeAttribute("loginUser");
        return ResponseEntity.ok("Log-Out");
    }
}
