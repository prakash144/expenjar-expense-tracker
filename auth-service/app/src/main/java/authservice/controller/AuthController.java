package authservice.controller;

import authservice.entities.RefreshToken;
import authservice.model.UserInfoDto;
import authservice.response.JwtResponseDTO;
import authservice.service.JwtService;
import authservice.service.RefreshTokenService;
import authservice.service.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@Slf4j
@AllArgsConstructor
@RestController
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @PostMapping("auth/v1/signup")
    public ResponseEntity<?> SignUp(@RequestBody UserInfoDto userInfoDto) {
        log.info("Received signup request for user: {}", userInfoDto.getUsername());

        try {
            String userId = userDetailsService.signupUser(userInfoDto);

            if (Objects.isNull(userId)) {
                log.warn("Signup failed: user already exists - {}", userInfoDto.getUsername());
                return new ResponseEntity<>("Already Exist", HttpStatus.BAD_REQUEST);
            }

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userInfoDto.getUsername());
            String jwtToken = jwtService.GenerateToken(userInfoDto.getUsername());

            log.info("User {} signed up successfully. UserID: {}", userInfoDto.getUsername(), userId);

            return new ResponseEntity<>(
                    JwtResponseDTO.builder()
                            .accessToken(jwtToken)
                            .token(refreshToken.getToken())
                            .userId(userId)
                            .build(),
                    HttpStatus.OK
            );

        } catch (Exception ex) {
            log.error("Exception during signup for user: {}", userInfoDto.getUsername(), ex);
            return new ResponseEntity<>("Exception in User Service", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/auth/v1/ping")
    public ResponseEntity<String> ping() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            String userId = userDetailsService.getUserByUsername(username);

            if (Objects.nonNull(userId)) {
                log.debug("Ping successful for user: {}", username);
                return ResponseEntity.ok(userId);
            } else {
                log.warn("Ping request from unauthenticated user: {}", username);
            }
        } else {
            log.warn("Unauthorized ping request received.");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
    }

    @GetMapping("/health")
    public ResponseEntity<Boolean> checkHealth() {
        log.debug("Health check requested");
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
}
