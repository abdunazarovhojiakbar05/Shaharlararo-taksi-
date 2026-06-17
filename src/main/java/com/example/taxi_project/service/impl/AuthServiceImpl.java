package com.example.taxi_project.service.impl;

import com.example.taxi_project.dto.auth.*;
import com.example.taxi_project.enums.UserRole;
import com.example.taxi_project.exceptions.ResourceNotFoundException;
import com.example.taxi_project.exceptions.UserAlreadyExistsException;
import com.example.taxi_project.model.User;
import com.example.taxi_project.repository.UserRepository;
import com.example.taxi_project.service.AuthService;
import com.example.taxi_project.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    @Override
    public User data(String firstName, String username, String code) {
        if (firstName == null) firstName = "foydalanuvchi";
        if (username == null) username = "foydalanuvchi";
        if (code == null) throw new RuntimeException("Kod bo'sh bo'lmasligi kerak");

        User user = new User();
        user.setName(firstName);
        user.setUsername(username);
        user.setCode(code);
        user.setRole(UserRole.USER);
        user.set_active(true);

        return userRepository.save(user);
    }

    @Override
    public String sendCode(String phone) {
        // Generatsiya: 6 xonali random kod
        String code = String.valueOf(100000 + new Random().nextInt(900000));
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(5);

        Optional<User> existing = userRepository.findUserByUsername(phone);
        User user;
        if (existing.isPresent()) {
            user = existing.get();
        } else {
            user = new User();
            user.setPhone(phone);
            user.setRole(UserRole.USER);
        }
        user.setCode(code);
        user.setExpired_at(expiredAt);
        userRepository.save(user);

        // TODO: SMS gateway orqali yuborish (Eskiz, Playmobile va h.k.)
        System.out.println("SMS kodi: " + code + " -> " + phone);

        return "Kod yuborildi: " + phone;
    }

    @Override
    public SendOtpResponse login(SendOtpRequest requestDto) {
        String phone = requestDto.getPhone();
        if (phone == null || phone.isBlank()) {
            throw new RuntimeException("Telefon raqami bo'sh bo'lishi mumkin emas");
        }

        // sendCode(phone);

        return SendOtpResponse.builder()
                .access_token(null)
                .refresh_token(null)
                .build();
    }

    @Override
    public SendOtpResponse registration(RegistrationRequestDto requestDto) {
        String phone = requestDto.getPhone();

        Optional<User> existing = userRepository.findUserByUsername(phone);
        if (existing.isPresent() && existing.get().is_active()) {
            throw new UserAlreadyExistsException("Bu raqam allaqachon ro'yxatdan o'tgan: " + phone);
        }

        String code = String.valueOf(100000 + new Random().nextInt(900000));
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(5);

        User user = existing.orElse(new User());
        user.setPhone(phone);
        user.setCode(code);
        user.setExpired_at(expiredAt);
        user.setRole(UserRole.USER);
        user.set_active(false); // OTP tasdiqlanmaguncha false
        userRepository.save(user);


        System.out.println("Ro'yxatdan o'tish kodi: " + code + " -> " + phone);

        return SendOtpResponse.builder()
                .access_token(null)
                .refresh_token(null)
                .build();
    }

    @Override
    public LoginResponseDto verifyOtpCode(VerifyOtpRequest requestDto) {


         User user = userRepository.findAll().stream()
                .filter(u -> u.getCode() != null)
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Foydalanuvchi topilmadi"));

        /* if (user.getExpired_at() == null || user.getExpired_at().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Kod muddati o'tib ketgan");
        }*/

        user.set_active(true);
        user.setCode("null");
        user.setExpired_at(null);
        userRepository.save(user);

        String access = jwtUtils.generateToken(user.getPhone());
        String refresh = jwtUtils.generateRefreshToken(user.getPhone());

        return LoginResponseDto.builder()
                .name(user.getName())
                .access_token(access)
                .refresh_token(refresh)
                .build();
    }



    @Override
    public RefreshTokenResponseDto refreshToken(RefreshTokenRequestDto dto) {
        String refreshToken = dto.getRefresh_token();

        if (!jwtUtils.validateToken(refreshToken)) {
            throw new RuntimeException("Refresh token yaroqsiz yoki muddati o'tgan");
        }

        String phone = jwtUtils.getUsernameFromToken(refreshToken);
        String newAccess = jwtUtils.generateToken(phone);
        String newRefresh = jwtUtils.generateRefreshToken(phone);

        return new RefreshTokenResponseDto();
    }

    @Override
    public void logout(String token) {
        // Stateless JWT uchun — token blacklist yoki Redis kerak
        // Hozircha validation qilish kifoya:
        if (!jwtUtils.validateToken(token)) {
            throw new RuntimeException("Token allaqachon yaroqsiz");
        }
        // TODO: Redis ga token qo'shish (blacklist)
        System.out.println("Logout bajarildi. Token bekor: " + token);
    }
}