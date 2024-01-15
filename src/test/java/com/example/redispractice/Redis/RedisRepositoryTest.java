package com.example.redispractice.Redis;

import com.example.redispractice.repository.PersonRepository;
import com.example.redispractice.vo.Address;
import com.example.redispractice.vo.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RedisRepositoryTest {

    @Autowired
    private PersonRepository redisRepository;

    @Test
    @DisplayName("RedisHash를 사용한 test")
    public void redisSaveTest(){
        // given
        Address address = new Address("서울특별시 강북구 수유2동");
        Person person = new Person("hi", "yang", "bin", address);

        // when
        Person savedPerson = redisRepository.save(person);

        // then
        Optional<Person> findPerson = redisRepository.findById(savedPerson.getId());
        System.out.println(findPerson.get());
        assertThat(findPerson.isPresent()).isEqualTo(Boolean.TRUE);
        assertThat(findPerson.get().getFirstName()).isEqualTo(person.getFirstName());
    }

    @Test
    @DisplayName("데이터 변경")
    public void redisUpdateTest(){
        // given
        Address address = new Address("서울특별시 강북구 수유2동");
        Person person = new Person("hi", "yang", "bin", address);
        redisRepository.save(person);

        Address address1 = new Address("서울특별시 강북구 수유1동");
        Person person1 = new Person("hi", "yang", "seung", address1);
        redisRepository.save(person1);

        //when
        Optional<Person> findPerson = redisRepository.findById("hi");

        //then
        assertThat(findPerson.get().getLastName()).isEqualTo("seung");
    }

    @Test
    @DisplayName("데이터 전체 반환 테스트")
    public void redisDataFindAllTest(){
        //given
        Address address = new Address("서울특별시 강북구 수유2동");
        Person person = new Person("hi1", "yang", "bin", address);
        redisRepository.save(person);

        Address address1 = new Address("서울특별시 강북구 수유1동");
        Person person1 = new Person("hi2", "yang", "seung", address1);
        redisRepository.save(person1);

        //when
        Iterable<Person>findAll = redisRepository.findAll();

        List<Person>list = new ArrayList<>();

        findAll.forEach(list::add);

        //then
        assertThat(list.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("데이터 삭제")
    public void redisDeleteTest(){
        //given
        Optional<Person>findPerson  = redisRepository.findById("hi");
        Person detail = findPerson.get();
        //when
        redisRepository.delete(detail);
    }

}
