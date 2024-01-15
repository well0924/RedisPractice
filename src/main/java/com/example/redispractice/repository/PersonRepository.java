package com.example.redispractice.repository;

import com.example.redispractice.vo.Person;
import org.springframework.data.repository.CrudRepository;

public interface PersonRepository extends CrudRepository<Person,String> {

}
