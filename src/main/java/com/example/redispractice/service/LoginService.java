package com.example.redispractice.service;

import com.example.redispractice.vo.dto.LoginDto;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Log4j2
@Service
@AllArgsConstructor
public class LoginService {

    private final HttpSession session;

    public static String MemberId = "SpringSession";

    public String login(LoginDto loginDto){
        session.setAttribute(MemberId,loginDto);
        log.info(session.getId());
        return session.getId();
    }

    public void logout(){
        session.removeAttribute(MemberId);
    }
}
