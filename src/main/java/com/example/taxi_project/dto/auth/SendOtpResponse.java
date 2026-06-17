package com.example.taxi_project.dto.auth;


import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SendOtpResponse {
    String  refresh_token;
    String  access_token;
}
