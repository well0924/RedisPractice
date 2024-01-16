package com.example.redispractice.Redis;

import com.example.redispractice.vo.Address;
import com.example.redispractice.vo.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class RedisTemplateTest {

    @Autowired
    RedisTemplate<String, Person>redisTemplate;

    @Test
    @DisplayName("RedisTemplate String test")
    public void redisStringTest(){
        //given
        Address address = new Address("서울특별시 강북구 수유2동 수유벽산아파트 7동 806호");
        Person person = new Person("hi","go","fu",address);
        ValueOperations<String,Person>stringPersonValueOperations = redisTemplate.opsForValue();

        //when
        stringPersonValueOperations.set("hi",person);

        //then
        Person person1 = redisTemplate.opsForValue().get("hi");

        System.out.println(person1);
        assertThat(person1).isNotNull();
    }

    @Test
    @DisplayName("redis List Test")
    public void redisListTest(){
        ListOperations<String,Person>listOperations = redisTemplate.opsForList();

        listOperations.rightPush("redisPersonList",new Person("ho","fu","hogae",new Address("to")));
        
        listOperations.rightPush("redisPersonList",new Person("ho1","fu","fugo",new Address("to1")));

        Long size = listOperations.size("redisPersonList");

        Person person = listOperations.index("redisPersonList",1);

        List<Person>list = listOperations.range("redisPersonList",0,1);

        System.out.println("리스트에 저장된 갯수:::"+size);
        System.out.println("특정 객체에 저장된 데이터:::"+person);
        System.out.println("전체 저장된 데이터:::"+list);
        
        assertThat(size).isEqualTo(2);

    }

    @Test
    @DisplayName("redisSet Test")
    public void redisSetTest(){
        SetOperations<String,Person>setOperations = redisTemplate.opsForSet();

        setOperations.add("set",new Person("hi","go","fe",new Address("aaa")));
        setOperations.add("set",new Person("hi","go1","fee",new Address("aaaa")));
        setOperations.add("set1",new Person("hi","go1","fee",new Address("a")));

        //Person person = setOperations.pop("set");

        //List<Person>list = setOperations.pop("set",2);

        Set<Person>personSet = setOperations.members("set");

        Boolean result = setOperations.isMember("set1",new Person("hi","go1","fee",new Address("a")));

        System.out.println(personSet);
        System.out.println(result);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("redisZSet Test")
    public void redisZSetTest(){
        ZSetOperations<String,Person>zSetOperations = redisTemplate.opsForZSet();

        zSetOperations.add("set",new Person("no","fu","hogae",new Address("asds")),1);
        zSetOperations.add("set",new Person("no1","fu","hogae",new Address("asds")),2);

        long result = zSetOperations.count("set",0,2);

        System.out.println(result);

        assertThat(result).isEqualTo(2);
    }

    @Test
    @DisplayName("redisHash Test")
    public void redisHashTest(){
        //key, hkey, value
        HashOperations<String,Object,Object>hashOperations = redisTemplate.opsForHash();

        hashOperations.put("hash","test1",new Person("ho1","aa","fu-go",new Address("aa")));

        Map<String,Person>hash = new HashMap<>();

        hash.put("ho2",new Person("ho2","aa","fu-go",new Address("aaa")));
        hash.put("ho3",new Person("ho2","aa","fu-go",new Address("aa")));

        hashOperations.putAll("hashAll",hash);

        Person p1 = (Person) hashOperations.get("hashAll","ho2");
        Person p2 = (Person) hashOperations.get("hashAll","ho3");

        Person p3 = (Person) hashOperations.get("hash","test1");

        System.out.println(p1);
        System.out.println(p2);
        System.out.println(p3);

    }

}
