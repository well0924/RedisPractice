package com.example.redispractice.Redis;

import com.example.redispractice.repository.PersonRepository;
import com.example.redispractice.repository.TestRepository;
import com.example.redispractice.service.RedisConcurrencyService;
import com.example.redispractice.vo.Address;
import com.example.redispractice.vo.Person;
import com.example.redispractice.vo.TestEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RedisRepositoryTest {

    @Autowired
    private PersonRepository redisRepository;

    @Autowired
    private RedisConcurrencyService redisConcurrencyService;

    @Autowired
    private TestRepository testRepository;

    @Test
    @DisplayName("RedisHash를 사용한 test")
    public void redisSaveTest(){
        // given
        Address address = new Address("서울특별시 강북구 xx2동");
        Person person = new Person("hi", "yang", "bin", 1L,address);

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
        Address address = new Address("서울특별시 강북구 xx2동");
        Person person = new Person("hi", "yang", "bin", 1L,address);
        redisRepository.save(person);

        Address address1 = new Address("서울특별시 강북구 xx1동");
        Person person1 = new Person("hi", "yang", "seung",1L, address1);
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
        Address address = new Address("서울특별시 강북구 xx2동");
        Person person = new Person("hi1", "yang", "bin",1L, address);
        redisRepository.save(person);

        Address address1 = new Address("서울특별시 강북구 xx1동");
        Person person1 = new Person("hi2", "yang", "seung",1L, address1);
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

    @Test
    @DisplayName("동시성 테스트->")
    public void 동시에_100명_테스트() throws InterruptedException {
        int threadCount =100;
        final ExecutorService executorService = Executors.newFixedThreadPool(32);
        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);

        for(int i=0;i<threadCount;i++){
            executorService.submit(()->{
                try{
                    redisConcurrencyService.countUp(1,1L);
                }catch (Exception e){
                    throw new RuntimeException();
                }finally {
                    countDownLatch.countDown();
                }
            });
        }

        countDownLatch.await();

        TestEntity testEntity = testRepository.findById(1).orElseThrow();
        System.out.println(testEntity.getContents());
        System.out.println(testEntity.getCount());
    }
}
