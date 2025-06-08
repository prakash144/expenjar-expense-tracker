package com.example.user_service.controller;

import com.example.user_service.entities.UserInfoDto;
import com.example.user_service.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private final UserService userService;

    @GetMapping("/user/v1/getUser")
    public ResponseEntity<UserInfoDto> getUser(@RequestBody UserInfoDto userInfoDto) {
        log.info("Received request to fetch user: {}", userInfoDto);
        try {
            UserInfoDto user = userService.getUser(userInfoDto);
            log.info("User fetched successfully: {}", user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching user: {}", e.getMessage(), e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/user/v1/createUpdate")
    public ResponseEntity<UserInfoDto> createUpdateUser(@RequestBody UserInfoDto userInfoDto) {
        log.info("Received request to create/update user: {}", userInfoDto);
        try {
            UserInfoDto user = userService.createOrUpdateUser(userInfoDto);
            log.info("User created/updated successfully: {}", user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Error creating/updating user: {}", ex.getMessage(), ex);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
