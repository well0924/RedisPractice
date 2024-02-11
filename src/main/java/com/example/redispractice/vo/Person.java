package com.example.redispractice.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import javax.persistence.Entity;
import java.io.Serializable;


//@RedisHash를 사용하면 단순 데이터를 저장하기보다는 객체를 저장을 하는데 사용을 한다.
//value 에 값을 넣으면 redis에서 확인을 했을시 키값으로 사용된다.
//ex) people이면 redis에서는  'people: key 값' 의  형태로 value가 저장이 된다.
@ToString
@RedisHash(value = "people",timeToLive = 10)
@Getter
@NoArgsConstructor
public class Person implements Serializable {
    //@Id는 org.springframework.data.annotation.Id 이고 RedisHash에서 key값으로 들어간다.
    //만약 저장을 할 때 따로 값을 넣어주지 않으면 렌덤의 값으로 들어가게 된다.
    @Id
    private String id;
    @Indexed
    private String firstName;
    private String lastName;
    private Address address;

    @Builder
    public Person(String id,String firstName,String lastName,Long count,Address address){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
    }

}
