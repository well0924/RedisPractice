package com.example.redispractice.service;

import com.example.redispractice.repository.TestRepository;
import com.example.redispractice.vo.TestEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class TestService {

    private final TestRepository testRepository;


    @Transactional(readOnly = true)
    public void countUp(Integer id){
        TestEntity detail = testRepository.findById(id).orElseThrow();
        //조회수 증가
        detail.countUp();
        testRepository.saveAndFlush(detail);
    }
}
