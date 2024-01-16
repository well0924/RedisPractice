package com.example.redispractice.vo.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {
    private String userId;
    private String password;
}
