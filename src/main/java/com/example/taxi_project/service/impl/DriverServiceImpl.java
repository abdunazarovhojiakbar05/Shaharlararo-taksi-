package com.example.taxi_project.service.impl;

import com.example.taxi_project.dto.driver.CarUpdate;
import com.example.taxi_project.dto.driver.DriverResponse;
import com.example.taxi_project.dto.driver.DriverUpdate;
import com.example.taxi_project.enums.DriverStatus;
import com.example.taxi_project.exceptions.ResourceNotFoundException;
import com.example.taxi_project.model.Driver;
import com.example.taxi_project.repository.DriverRepository;
import com.example.taxi_project.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;

    @Override
    public DriverResponse getById(UUID id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Haydovchi topilmadi: " + id));

        return toResponse(driver);
    }

    @Override
    public DriverResponse update(UUID id, DriverUpdate updateDto) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Haydovchi topilmadi: " + id));

        // DriverUpdate ichida name, username va h.k. bo'lishi kerak
        // Hozir bo'sh — quyidagilarni qo'shgandan keyin ishlaydi:
        // if (updateDto.getName() != null) driver.setName(updateDto.getName());
        // if (updateDto.getUsername() != null) driver.setUsername(updateDto.getUsername());

        Driver saved = driverRepository.save(driver);
        return toResponse(saved);
    }

    @Override
    public void changeStatus(UUID id, DriverStatus status) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Haydovchi topilmadi: " + id));

        // Driver modeliga DriverStatus maydoni qo'shish kerak:
        // driver.setStatus(status);
        // driverRepository.save(driver);

        // Hozircha log:
        System.out.println("Haydovchi " + id + " holati o'zgardi: " + status);
    }

    @Override
    public BigDecimal getBalance(UUID id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Haydovchi topilmadi: " + id));

        // Driver modeliga balance maydoni qo'shish kerak:
        // return driver.getBalance();
        return BigDecimal.ZERO; // Hozircha
    }

    @Override
    public void updateCarInfo(UUID id, CarUpdate carUpdateDto) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Haydovchi topilmadi: " + id));

        // Car entity ga @Entity va @ManyToOne qo'shing, keyin:
        // Car car = driver.getCar();
        // if (carUpdateDto.getModel() != null) car.setModel(carUpdateDto.getModel());
        // carRepository.save(car);

        System.out.println("Haydovchi " + id + " mashinasi yangilandi");
    }

    @Override
    public Double getRating(UUID id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Haydovchi topilmadi: " + id));

        return driver.getRating();
    }

    // Helper method
    private DriverResponse toResponse(Driver driver) {
        // DriverResponse hozir bo'sh — quyidagilarni qo'shing:
        // return DriverResponse.builder()
        //         .id(driver.getId())
        //         .name(driver.getName())
        //         .phone(driver.getPhone())
        //         .rating(driver.getRating())
        //         .build();
        return new DriverResponse();
    }
}