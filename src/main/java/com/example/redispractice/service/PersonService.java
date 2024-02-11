package com.example.redispractice.service;

import com.example.redispractice.repository.PersonRepository;
import com.example.redispractice.vo.Person;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    public void countUp(String id,Long count){
        Optional<Person>detail = personRepository.findById(id);
    }
}
