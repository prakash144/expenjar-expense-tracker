package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.entities.RefreshToken;
import org.example.request.AuthRequestDTO;
import org.example.request.RefreshTokenRequestDTO;
import org.example.response.JwtResponseDTO;
import org.example.service.JwtService;
import org.example.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Controller
public class TokenController
{

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("auth/v1/login")
    public ResponseEntity AuthenticateAndGetToken(@RequestBody AuthRequestDTO authRequestDTO) {
        log.info("Login attempt for user: {}", authRequestDTO.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequestDTO.getUsername(),
                        authRequestDTO.getPassword()
                )
        );

        log.info("Authentication object received: {}", authentication);

        if (authentication.isAuthenticated()) {
            log.info("Authentication successful for user: {}", authRequestDTO.getUsername());

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequestDTO.getUsername());
            log.info("Refresh token created for user: {}", authRequestDTO.getUsername());

            String accessToken = jwtService.GenerateToken(authRequestDTO.getUsername());
            log.info("Access token generated for user: {}", authRequestDTO.getUsername());

            JwtResponseDTO jwtResponse = JwtResponseDTO.builder()
                    .accessToken(accessToken)
                    .token(refreshToken.getToken())
                    .build();

            log.info("Returning successful login response for user: {}", authRequestDTO.getUsername());
            return new ResponseEntity<>(jwtResponse, HttpStatus.OK);
        } else {
            log.warn("Authentication failed for user: {}", authRequestDTO.getUsername());
            return new ResponseEntity<>("Exception in User Service", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("auth/v1/refreshToken")
    public JwtResponseDTO refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        log.info("Refresh token request received");

        return refreshTokenService.findByToken(refreshTokenRequestDTO.getToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserInfo)
                .map(userInfo -> {
                    String accessToken = jwtService.GenerateToken(userInfo.getUsername());
                    log.info("Refresh token successful for user: {}", userInfo.getUsername());
                    return JwtResponseDTO.builder()
                            .accessToken(accessToken)
                            .token(refreshTokenRequestDTO.getToken())
                            .build();
                })
                .orElseThrow(() -> {
                    log.error("Refresh token not found or expired: {}", refreshTokenRequestDTO.getToken());
                    return new RuntimeException("Refresh Token is not in DB..!!");
                });
    }

}
