package com.example.redispractice.repository;

import com.example.redispractice.vo.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<TestEntity,Integer> {
}
