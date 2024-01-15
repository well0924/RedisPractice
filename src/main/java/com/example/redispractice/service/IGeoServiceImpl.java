package com.example.redispractice.service;

import com.example.redispractice.vo.City;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.simple.JSONValue;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Log4j2
@Service
@AllArgsConstructor
public class IGeoServiceImpl implements IGeoService{

    private final String GEO_KEY = "ah-cities";

    private final StringRedisTemplate redisTemplate;


    //도시 저장
    @Override
    public Long saveCityInfoToRedis(Collection<City> cityInfos) {
        log.info("start to save city info: {}.", JSONValue.toJSONString(cityInfos));

        GeoOperations<String, String> ops = redisTemplate.opsForGeo();

        Set<RedisGeoCommands.GeoLocation<String>> locations = new HashSet<>();
        cityInfos.forEach(ci -> locations.add(new RedisGeoCommands.GeoLocation<>(
                ci.getCity(), new Point(ci.getLongitude(), ci.getLatitude())
        )));

        log.info("done to save city info.");

        return ops.add(GEO_KEY, locations);
    }

    //도시 조회(List)
    @Override
    public List<Point> getCityPos(String[] cities) {
        GeoOperations<String, String> ops = redisTemplate.opsForGeo();
        return ops.position(GEO_KEY, cities);
    }

    //도시간의 거리
    @Override
    public Distance getTwoCityDistance(String city1, String city2, Metric metric) {
        GeoOperations<String, String> ops = redisTemplate.opsForGeo();
        return metric == null ? ops.distance(GEO_KEY, city1, city2) : ops.distance(GEO_KEY, city1, city2, metric);
    }

    //두지점의 거리
    @Override
    public GeoResults<RedisGeoCommands.GeoLocation<String>> getPointRadius(Circle within, RedisGeoCommands.GeoRadiusCommandArgs args) {
        GeoOperations<String, String> ops = redisTemplate.opsForGeo();
        return args == null ? ops.radius(GEO_KEY, within) : ops.radius(GEO_KEY, within, args);
    }

    //특정 회원의 거리
    @Override
    public GeoResults<RedisGeoCommands.GeoLocation<String>> getMemberRadius(String member, Distance distance, RedisGeoCommands.GeoRadiusCommandArgs args) {
        GeoOperations<String, String> ops = redisTemplate.opsForGeo();
        return args == null ? ops.radius(GEO_KEY, member, distance) : ops.radius(GEO_KEY, member, distance, args);
    }

    //도시 정보 조회(Hash)
    @Override
    public List<String> getCityGeoHash(String[] cities) {
        GeoOperations<String, String> ops = redisTemplate.opsForGeo();
        return ops.hash(GEO_KEY, cities);
    }

}
