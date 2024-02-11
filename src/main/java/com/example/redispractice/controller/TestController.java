package com.example.redispractice.controller;

import com.example.redispractice.repository.TestRepository;
import com.example.redispractice.service.LoginService;
import com.example.redispractice.service.RedisConcurrencyService;
import com.example.redispractice.service.TestService;
import com.example.redispractice.vo.TestEntity;
import com.example.redispractice.vo.dto.LoginDto;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@AllArgsConstructor
public class TestController {
    private final TestRepository testRepository;

    private final TestService testService;

    private final LoginService loginService;

    //redsi session을 활용한 로그인
    @PostMapping("/login")
    ResponseEntity<String>login(@RequestBody LoginDto loginDto){
        return ResponseEntity.ok(loginService.login(loginDto));
    }

    @GetMapping("/logout")
    public ResponseEntity<String>logout(){
        //세션제거
        loginService.logout();
        return ResponseEntity.ok("Log-Out");
    }

    @GetMapping("/{id}")
    public ResponseEntity<?>detail(@PathVariable("id")Integer id) throws InterruptedException {
        TestEntity testEntity = testRepository.findById(id).orElseThrow();
        testService.countUp(id);
        return ResponseEntity.ok(testEntity.getCount());
    }
}
