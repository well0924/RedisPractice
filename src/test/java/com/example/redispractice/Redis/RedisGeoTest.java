package com.example.redispractice.Redis;

import com.example.redispractice.service.IGeoService;
import com.example.redispractice.vo.City;
import org.json.simple.JSONValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class RedisGeoTest {

    private List<City> cityInfos;

    @Autowired
    private IGeoService geoService;

    @BeforeEach
    public void init() {

        cityInfos = new ArrayList<>();

        cityInfos.add(new City("hefei", 117.17, 31.52));
        cityInfos.add(new City("anqing", 117.02, 30.31));
        cityInfos.add(new City("huaibei", 116.47, 33.57));
        cityInfos.add(new City("suzhou", 116.58, 33.38));
        cityInfos.add(new City("fuyang", 115.48, 32.54));
        cityInfos.add(new City("bengbu", 117.21, 32.56));
        cityInfos.add(new City("huangshan", 118.18, 29.43));
    }

    @Test
    @DisplayName("도시정보를 redis에 저장")
    public void testSaveCityInfoToRedis() {

        System.out.println(geoService.saveCityInfoToRedis(cityInfos));
    }

    @Test
    @DisplayName("저장된 도시를 리스트로 조회하기.")
    public void testGetCityPos() {

        System.out.println(JSONValue.toJSONString(geoService.getCityPos(
                Arrays.asList("anqing", "suzhou", "xxx").toArray(new String[3])
        )));
    }

    @Test
    @DisplayName("2개의 도시간의 거리를 측정")
    public void testGetTwoCityDistance(){

        System.out.println(geoService.getTwoCityDistance("anqing", "suzhou", Metrics.KILOMETERS).getValue());
    }

    @Test
    @DisplayName("특정반경의 거리에 있는 도시를 찾기")
    public void testGetPointRadius() {

        Point center = new Point(cityInfos.get(0).getLongitude(), cityInfos.get(0).getLatitude());
        Distance radius = new Distance(200, Metrics.KILOMETERS);
        Circle within = new Circle(center, radius);

        System.out.println(JSONValue.toJSONString(geoService.getPointRadius(within, null)));

        // order by 거리 limit 2, 중심점으로부터 거리를 조회한다.
        RedisGeoCommands.GeoRadiusCommandArgs args =
                RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance().limit(2).sortAscending();
        System.out.println(JSONValue.toJSONString(geoService.getPointRadius(within, args)));
    }

    @Test
    @DisplayName("특정 회원을 기준으로 특정거리내에 있는 도시검색")
    public void testGetMemberRadius() {

        Distance radius = new Distance(200, Metrics.KILOMETERS);

        System.out.println(JSONValue.toJSONString(geoService.getMemberRadius("suzhou", radius, null)));

        // order by 거리 limit 2, 중심점으로부터 거리를 조회한다.
        RedisGeoCommands.GeoRadiusCommandArgs args =
                RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeDistance().limit(2).sortAscending();
        System.out.println(JSONValue.toJSONString(geoService.getMemberRadius("suzhou", radius, args)));
    }

    @Test
    @DisplayName("해시타입으로 도시를 저장하기.")
    public void testGetCityGeoHash() {

        System.out.println(JSONValue.toJSONString(geoService.getCityGeoHash(
                Arrays.asList("anqing", "suzhou", "xxx").toArray(new String[3])
        )));
    }
}
