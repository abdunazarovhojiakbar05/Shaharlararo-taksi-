package com.example.taxi_project.dto.auth;

 import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto {
    String access_token;
    String refresh_token;
    String name;
}