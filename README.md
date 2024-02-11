# Redis 실습

Redis 를 배우기 위한 실습 리포지터리입니다.

## 순서 

- [x]  RedisRepository vs RedisTemplate

- [x] Redis 자료구조 (String,Hash,Set,List,Geo)

- [x] Redis Session
     
- [ ] Redis 분산락

- [ ] Redis pub/sub


## RedisRepository vs RedisTemplate

Redis를 사용하는데 있어서는 RedisRepository를 사용하는 방법과 RedisTemplate 를 사용하는 방법이 있습니다.

우선 RedisRepository는 spring에서 제공하는 crudRepository를 상속 받아서 사용하는 방법이고 RedisTemplate를 사용하는 방법은 

RedisTemplate 클래스를 사용하는 방법으로 되어 있다. 지금부터 2가지 방법을 사용하는 방법을 작성하려고 한다.

1. Redis 의존성을 주입을 한다. 

``````
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    //redis 
    implementation 'org.springframework.boot:spring-boot-starter-data-redis'
    implementation 'org.springframework.session:spring-session-data-redis'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    // https://mvnrepository.com/artifact/com.googlecode.json-simple/json-simple
    implementation 'com.googlecode.json-simple:json-simple:1.1.1'
    compileOnly 'org.projectlombok:lombok'
    runtimeOnly 'org.mariadb.jdbc:mariadb-java-client'
    developmentOnly 'org.springframework.boot:spring-boot-devtools'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
``````

2. RedisConfig 클래스 작성하기

``````
@Configuration
@EnableCaching
@EnableRedisHttpSession
@EnableRedisRepositories(enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP) // RedisRepository + Secondary Index 적용
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Value("${spring.redis.password}")
    private String password;
    
    
    /**
     * 세션 설정 
     **/
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setPort(redisPort);
        redisStandaloneConfiguration.setHostName(redisHost);
        redisStandaloneConfiguration.setPassword(password);
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }
    
    /**
     * RedisTemplate 설정 
     **/
    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }
    
    /**
     * Session 직렬화 설정
     **/
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }

}

//application.properties
#redis
spring.redis.port=6379
spring.redis.host=localhost
spring.redis.password=1234
spring.session.store-type=redis
``````

위의 설정 클래스를 설명을 하면 이러하다. 

@EnableRedisHttpSession : redis Session을 사용하기 위해서 선언한 어노테이션
@EnableRedisRepositories : redisRepository를 사용하기 위해서 선언한 어노테이션

@Value 를 통해서 redis의 포트번호와 host값 redis의 비밀번호를 설정파일에 있는 값을 가져온다. 

그 다음 LettuceConnectionFactory를 생성하여 bean으로 등록합니다.


3. Redis에 저장할 객체를 @RedisHash 를 사용해서 구현하기.

````````
@ToString
@RedisHash(value = "people",timeToLive = 10)
@Getter
@NoArgsConstructor
public class Person implements Serializable {
    //@Id는 org.springframework.data.annotation.Id 이고 RedisHash에서 key값으로 들어간다.
    //만약 저장을 할 때 따로 값을 넣어주지 않으면 렌덤의 값으로 들어가게 된다.
    @Id
    private String id;
    private String firstName;
    private String lastName;

    private Address address;

    @Builder
    public Person(String id,String firstName,String lastName,Address address){
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
    }


}
````````

위의 코드를 설명하자면 @RedisHash를 사용하면 단순 데이터를 저장하기보다는 객체를 저장을 하는데 사용을 한다.

@RedisHash에 있는 value는 redis에 저장이 되었을시 key값으로 저장이 된다. 

ex) @RedisHash(value="people") 이면 people : key값으로 value가 저장이 된다.

timeToLive 는 ttl로 레디스에 저장되는 유효기간을 말합니다. 

@Id는 RedisHash에서 key값으로 들어간다. 만약 저장을 할 때 따로 값을 넣어주지 않으면 렌덤의 값으로 들어가게 된다.


4. RedisRepository 구현하기.

``````````
public interface PersonRepository extends CrudRepository<Person,String> {

}
``````````
RedisRepository를 구현하는 방법은 CrudRepository를 상속받으면 간단하게 끝난다.

5. RedisRepository를 활용한 crud

````````````
@SpringBootTest
public class RedisRepositoryTest {

    @Autowired
    private PersonRepository redisRepository;

    @Test
    @DisplayName("RedisHash를 사용한 test")
    public void redisSaveTest(){
        // given
        Address address = new Address("서울특별시 강북구 xx2동");
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
        Address address = new Address("서울특별시 강북구 xx2동");
        Person person = new Person("hi", "yang", "bin", address);
        redisRepository.save(person);

        Address address1 = new Address("서울특별시 강북구 xx1동");
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
        Address address = new Address("서울특별시 강북구 xx2동");
        Person person = new Person("hi1", "yang", "bin", address);
        redisRepository.save(person);

        Address address1 = new Address("서울특별시 강북구 xx1동");
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
````````````

6. RedisTemplate를 사용

우선은 RedisTemplate를 사용하려면 RedisConfig에 RedisTemplate를 Bean으로 등록을 해야 한다.

````````````
    /**
     * RedisTemplate 설정 
     **/
    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }
````````````

7. RedisTemplate 사용예

``````````
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
``````````

## Redis 자료구조

Redis는 다양한 자료구조를 가지고 있다. 다음은 Redis에서 지원하고 있는 자료구조입니다. 

Strings : Vinary-safe한 기본적인 key-value 구조

Lists : String element의 모음, 순서는 삽입된 순서를 유지하며 기본적인 자료구로 Linked List를 사용

Sets : 유일한 값들의 모임인 자료구조, 순서는 유지되지 않음

Sorted sets : Sets 자료구조에 score라는 값을 추가로 두어 해당 값을 기준으로 순서를 유지

Hahses : 내부에 key-value 구조를 하나더 가지는 Reids 자료구조

Bit arrays(bitMaps) : bit array를 다를 수 있는 자료구조

HyperLogLogs : HyperLogLog는 집합의 원소의 개수를 추정하는 방법, Set 개선된 방법

Streams : Redis 5.0 에서 Log나 IoT 신호와 같이 지속적으로 빠르게 발생하는 데이터를 처리하기 위해서 도입된 자료구조


## RedisSession

RedisSession은 주로 웹 애플리케이션과 같은 서버 기반 애플리케이션에서 세션 데이터를 저장하고 관리하는 데 사용됩니다.

보통 RedisSession의 특징을 말하자면 다음과 같습니다. 

##### 빠른 응답 속도:
레디스는 메모리 기반의 데이터 저장소이기 때문에 매우 빠른 응답 속도를 제공합니다. 세션 데이터를 레디스에 저장하면 데이터를 빠르게 검색하고 업데이트할 수 있어 사용자 경험을 향상시킬 수 있습니다.

##### 확장성:
레디스는 분산 환경에서 확장 가능하며, 클러스터 구성을 통해 데이터베이스의 성능과 확장성을 향상시킬 수 있습니다. 따라서 대규모 애플리케이션에서도 세션 데이터를 효율적으로 관리할 수 있습니다.

##### 영속성과 메모리 관리:
레디스는 데이터를 영속적으로 저장할 수 있는 기능을 제공하며, 동시에 메모리를 효율적으로 관리합니다. 세션 데이터는 보통 세션이 유효한 동안만 필요하므로, 메모리를 효과적으로 활용할 수 있습니다.

##### 높은 가용성:
레디스 클러스터 구성을 사용하면 고가용성을 제공할 수 있습니다. 세션 데이터의 안정성과 가용성이 중요한 경우 레디스를 사용하여 세션 관리를 할 수 있습니다.

##### 분산 환경에서의 공유:
여러 서버나 마이크로서비스 간에 세션 데이터를 공유할 때 레디스는 효과적인 솔루션입니다. 분산 환경에서 여러 서버가 같은 세션 데이터에 접근하고 갱신할 수 있습니다.

우선 RedisSession 을 사용하기 위해서는 비밀번호를 설정을 해야 합니다.  

세션을 설정을 하는 방법은 redis-cli에서

###### config set requirepass '설정하고자하는 비밀번호'

###### auth '설정된 비밀번호' 을 입력한 뒤에 실행을 하면 된다.

#### 1. RedisSession 설정 방법

어플리케이션 부분에 RedisSession 어노테이션을 설정.

````
@EnableRedisHttpSession // RedisSession 설정 어노테이션
@SpringBootApplication
public class RedisPracticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisPracticeApplication.class, args);
    }

}
````

RedisSession 설정 클래스

````
@Configuration
@EnableCaching //캐시 적용
@EnableRedisRepositories(enableKeyspaceEvents = RedisKeyValueAdapter.EnableKeyspaceEvents.ON_STARTUP) // RedisRepository + Secondary Index 적용
public class RedisSessionConfig {
    
    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Value("${spring.redis.password}")
    private String password;
    
    
    /**
     * 세션 설정 
     **/
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setPort(redisPort);
        redisStandaloneConfiguration.setHostName(redisHost);
        redisStandaloneConfiguration.setPassword(password);
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }
    
    /**
     * RedisTemplate 설정 
     **/
    @Bean
    public RedisTemplate<?, ?> redisTemplate() {
        RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        return redisTemplate;
    }
    
    /**
     * Session 직렬화 설정
     **/
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }

}

````
#### 2.로그인 관련 서비스 & 컨트롤러 코드

````
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

@RestController
@AllArgsConstructor
public class TestController {
    private final TestRepository testRepository;

    private final TestService testService;

    private final LoginService loginService;

    //redsi session을 활용한 로그인
    @PostMapping("/login")
    ResponseEntity<String>login(@RequestBody LoginDto loginDto){
        return ResponseEntity.ok(loginService.login(loginDto));
    }

    @GetMapping("/logout")
    public ResponseEntity<String>logout(){
        //세션제거
        loginService.logout();
        return ResponseEntity.ok("Log-Out");
    }
}
````

#### 3.로그인 결과

로그인을 한 후에 redis-cli로 들어가면 로그인을 한 세션정보가 들어와 있는 것을 볼 수가 있다.

![redisSession1](https://github.com/well0924/RedisPractice/assets/89343159/a5a6276a-5465-4121-90dc-4c9dfbafe76d)

사진에 있는 부분을 설명하면 다음과 같다.

- spring:session:sessions:2a44bdca-64f1-454c-8e0d-0a1be3c70a8b

: **세션생성시간**, **최근조회시간**, **최대 타임아웃 허용시간**,**세션의 저장된 데이터**가 있다.

: 타입은 **hash** 타입이고 해당 세션을 조회를 하려면 **hgetall**이라는 명령어를 사용해서 세션의 내용을 조회하면 된다.

![redisSession2](https://github.com/well0924/RedisPractice/assets/89343159/e07917f6-cb2e-40f4-8be2-f787b8390cb6)

- spring:session:expirations:1707641940000

: 해당시간이 되면 세션이 만료가 된다. 

: 타입은 **set**이다. 조회를 하는 방법은 SMEMBERS key값을 사용하면 조회를 할 수 있다. 


- **spring:session:sessions:expires:2a44bdca-64f1-454c-8e0d-0a1be3c70a8b**

: 해당 세션의 만료키로 사용되는 키.

: 타입은 String이고 조회를 하려면 get key값으로 조회를 한다.

## Redis 분산락



