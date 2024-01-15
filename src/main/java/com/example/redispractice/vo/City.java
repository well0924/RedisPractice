package com.example.redispractice.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class City {
    private String city;
    
    //경도
    private Double longitude;

    //위도
    private Double latitude;
}
