package com.prakash.userservice.controller;

import com.prakash.userservice.entities.UserInfoDto;
import com.prakash.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/createUpdate")
    public ResponseEntity<UserInfoDto> createUpdateUser(@RequestBody UserInfoDto userInfoDto) {
        try {
            log.info("Creating/updating user: {}", userInfoDto.getUserId());
            UserInfoDto user = userService.createOrUpdateUser(userInfoDto);
            return ResponseEntity.ok(user);
        } catch (Exception ex) {
            log.error("Error creating/updating user: {}", userInfoDto.getUserId(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserInfoDto>> getAllUsers() {
        try {
            log.info("Fetching all users...");
            List<UserInfoDto> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception ex) {
            log.error("Error while fetching all users", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Boolean> checkHealth() {
        return ResponseEntity.ok(true);
    }
}
