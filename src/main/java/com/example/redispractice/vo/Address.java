package com.example.redispractice.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Getter
@ToString
@NoArgsConstructor
public class Address implements Serializable {
    private String address;

    public Address(String address){
        this.address = address;
    }
}
