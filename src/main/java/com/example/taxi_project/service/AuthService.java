package com.example.taxi_project.service;

import com.example.taxi_project.dto.auth.*;
import com.example.taxi_project.model.User;

public interface AuthService {
    User data(String firstName, String username, String code);

    String sendCode(String phone);

    void logout(String token);

    RefreshTokenResponseDto refreshToken(RefreshTokenRequestDto dto);

    SendOtpResponse registration(RegistrationRequestDto requestDto);

    LoginResponseDto verifyOtpCode(VerifyOtpRequest requestDto);

    SendOtpResponse login(SendOtpRequest requestDto);
}
