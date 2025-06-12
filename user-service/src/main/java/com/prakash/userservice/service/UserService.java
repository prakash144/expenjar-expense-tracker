package com.prakash.userservice.service;

import com.prakash.userservice.entities.UserInfo;
import com.prakash.userservice.entities.UserInfoDto;
import com.prakash.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * Create or update a user based on userId presence.
     */
    public UserInfoDto createOrUpdateUser(UserInfoDto userInfoDto) {
        log.info("Request received to create or update user: {}", userInfoDto.getUserId());

        try {
            UnaryOperator<UserInfo> updateUser = user -> {
                log.debug("Updating existing user with ID: {}", userInfoDto.getUserId());
                return userRepository.save(userInfoDto.transformToUserInfo());
            };

            Supplier<UserInfo> createUser = () -> {
                log.debug("Creating new user as no existing user found with ID: {}", userInfoDto.getUserId());
                return userRepository.save(userInfoDto.transformToUserInfo());
            };

            UserInfo userInfo = userRepository.findByUserId(userInfoDto.getUserId())
                    .map(updateUser)
                    .orElseGet(createUser);

            log.info("User saved successfully with ID: {}", userInfo.getUserId());

            return convertToDto(userInfo);
        } catch (Exception ex) {
            log.error("Failed to create or update user: {}", userInfoDto.getUserId(), ex);
            throw ex;
        }
    }

    /**
     * Fetch user by userId provided in DTO.
     */
    public UserInfoDto getUser(UserInfoDto userInfoDto) throws Exception {
        String userId = userInfoDto.getUserId();
        log.info("Fetching user by ID: {}", userId);

        return userRepository.findByUserId(userId)
                .map(this::convertToDto)
                .orElseThrow(() -> {
                    log.warn("User not found for ID: {}", userId);
                    return new Exception("User not found");
                });
    }

    /**
     * Fetch all users.
     */
    public List<UserInfoDto> getAllUsers() {
        log.info("Fetching all users...");
        List<UserInfo> users = (List<UserInfo>) userRepository.findAll();
        return users.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    /**
     * Convert UserInfo entity to UserInfoDto.
     */
    private UserInfoDto convertToDto(UserInfo userInfo) {
        return new UserInfoDto(
                userInfo.getUserId(),
                userInfo.getFirstName(),
                userInfo.getLastName(),
                userInfo.getPhoneNumber(),
                userInfo.getEmail(),
                userInfo.getProfilePic()
        );
    }
}

