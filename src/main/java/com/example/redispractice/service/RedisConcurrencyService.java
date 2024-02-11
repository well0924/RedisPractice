package com.example.redispractice.service;

import com.example.redispractice.repository.RedisConcurrencyRepository;
import com.example.redispractice.repository.TestRepository;
import com.example.redispractice.vo.TestEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RedisConcurrencyService {

    private final TestService testService;

    private final RedisConcurrencyRepository repository;

    public void countUp(Integer id,Long key)throws InterruptedException{
        while (!repository.lock(key)){
            Thread.sleep(3000);
        }

        try{
            testService.countUp(id);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
