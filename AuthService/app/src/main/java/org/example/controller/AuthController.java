package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.entities.RefreshToken;
import org.example.model.UserInfoDto;
import org.example.response.JwtResponseDTO;
import org.example.service.JwtService;
import org.example.service.RefreshTokenService;
import org.example.service.UserDetailsServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
public class AuthController
{

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
            Boolean isSignUped = userDetailsService.signupUser(userInfoDto);
            if (Boolean.FALSE.equals(isSignUped)) {
                log.warn("Signup failed: user {} already exists", userInfoDto.getUsername());
                return new ResponseEntity<>("Already Exist", HttpStatus.BAD_REQUEST);
            }

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userInfoDto.getUsername());
            String jwtToken = jwtService.GenerateToken(userInfoDto.getUsername());

            log.info("Signup successful for user: {}", userInfoDto.getUsername());

            return new ResponseEntity<>(
                    JwtResponseDTO.builder()
                            .accessToken(jwtToken)
                            .token(refreshToken.getToken())
                            .build(),
                    HttpStatus.OK
            );
        } catch (Exception ex) {
            log.error("Exception during signup for user {}: {}", userInfoDto.getUsername(), ex.getMessage(), ex);
            return new ResponseEntity<>("Exception in User Service", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
