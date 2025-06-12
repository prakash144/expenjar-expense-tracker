package authservice.service;

import authservice.entities.UserInfo;
import authservice.eventProducer.UserInfoEvent;
import authservice.eventProducer.UserInfoProducer;
import authservice.model.UserInfoDto;
import authservice.repository.UserRepository;
import authservice.utils.ValidationUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Data
@Component
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private final UserInfoProducer userInfoProducer;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Attempting to load user by username: {}", username);
        UserInfo user = userRepository.findByUsername(username);

        if (user == null) {
            log.error("User not found with username: {}", username);
            throw new UsernameNotFoundException("Could not find user.");
        }

        log.info("User {} authenticated successfully", username);
        return new CustomUserDetails(user);
    }

    public UserInfo checkIfUserAlreadyExist(UserInfoDto userInfoDto) {
        log.debug("Checking if user already exists: {}", userInfoDto.getUsername());
        return userRepository.findByUsername(userInfoDto.getUsername());
    }

    public String signupUser(UserInfoDto userInfoDto) {
        log.info("Sign up request received for username: {}", userInfoDto.getUsername());

        try {
            ValidationUtil.validateUserAttributes(userInfoDto);
        } catch (IllegalArgumentException e) {
            log.warn("Validation failed for user sign-up: {}", e.getMessage());
            throw e;
        }

        if (Objects.nonNull(checkIfUserAlreadyExist(userInfoDto))) {
            log.warn("User already exists: {}", userInfoDto.getUsername());
            return null;
        }

        userInfoDto.setPassword(passwordEncoder.encode(userInfoDto.getPassword()));
        String userId = UUID.randomUUID().toString();
        UserInfo userInfo = new UserInfo(userId, userInfoDto.getUsername(), userInfoDto.getPassword(), new HashSet<>());
        userRepository.save(userInfo);

        log.info("New user created with ID: {}", userId);
        userInfoProducer.sendEventToKafka(userInfoEventToPublish(userInfoDto, userId));
        return userId;
    }

    public String getUserByUsername(String username) {
        log.debug("Fetching userId by username: {}", username);
        return Optional.ofNullable(userRepository.findByUsername(username))
                .map(UserInfo::getUserId)
                .orElse(null);
    }

    private UserInfoEvent userInfoEventToPublish(UserInfoDto userInfoDto, String userId) {
        return UserInfoEvent.builder()
                .userId(userId)
                .firstName(userInfoDto.getUsername())
                .lastName(userInfoDto.getLastName())
                .email(userInfoDto.getEmail())
                .phoneNumber(userInfoDto.getPhoneNumber())
                .build();
    }
}