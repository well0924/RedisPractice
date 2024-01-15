package com.example.redispractice.service;

import com.example.redispractice.vo.City;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;

import java.util.Collection;
import java.util.List;

public interface IGeoService {


    Long saveCityInfoToRedis(Collection<City> cityInfos);

    List<Point> getCityPos(String[] cities);

    Distance getTwoCityDistance(String city1, String city2, Metric metric);

    GeoResults<RedisGeoCommands.GeoLocation<String>> getPointRadius(
            Circle within, RedisGeoCommands.GeoRadiusCommandArgs args);

    GeoResults<RedisGeoCommands.GeoLocation<String>> getMemberRadius(
            String member, Distance distance, RedisGeoCommands.GeoRadiusCommandArgs args);

    List<String> getCityGeoHash(String[] cities);

}
