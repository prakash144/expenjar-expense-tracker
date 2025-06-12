package authservice.controller;

import authservice.entities.RefreshToken;
import authservice.request.AuthRequestDTO;
import authservice.request.RefreshTokenRequestDTO;
import authservice.response.JwtResponseDTO;
import authservice.service.JwtService;
import authservice.service.RefreshTokenService;
import authservice.service.UserDetailsServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.security.authentication.BadCredentialsException;

import java.util.Objects;

@Slf4j
@Controller
public class TokenController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("auth/v1/login")
    public ResponseEntity<?> authenticateAndGetToken(@RequestBody AuthRequestDTO authRequestDTO) {
        log.info("Login request received for user: {}", authRequestDTO.getUsername());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequestDTO.getUsername(),
                            authRequestDTO.getPassword()
                    )
            );

            if (!authentication.isAuthenticated()) {
                log.warn("Authentication failed for user: {}", authRequestDTO.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(authRequestDTO.getUsername());
            String userId = userDetailsService.getUserByUsername(authRequestDTO.getUsername());

            if (Objects.isNull(userId)) {
                log.warn("Authenticated user but user ID not found for username: {}", authRequestDTO.getUsername());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            if (Objects.isNull(refreshToken)) {
                log.warn("Authenticated user but failed to generate refresh token for: {}", authRequestDTO.getUsername());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not generate refresh token");
            }

            log.info("User {} authenticated successfully", authRequestDTO.getUsername());

            JwtResponseDTO response = JwtResponseDTO.builder()
                    .accessToken(jwtService.GenerateToken(authRequestDTO.getUsername()))
                    .token(refreshToken.getToken())
                    .userId(userId)
                    .build();

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException ex) {
            log.warn("Invalid credentials for user: {}", authRequestDTO.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");

        } catch (UsernameNotFoundException ex) {
            log.warn("User not found: {}", authRequestDTO.getUsername());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");

        } catch (Exception ex) {
            log.error("Unexpected error during login for user: {}", authRequestDTO.getUsername(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error during authentication");
        }
    }

    @PostMapping("auth/v1/refreshToken")
    public JwtResponseDTO refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequestDTO) {
        log.info("Refresh token request received");

        return refreshTokenService.findByToken(refreshTokenRequestDTO.getToken())
                .map(refreshToken -> {
                    log.debug("Token found in DB. Verifying expiration.");
                    return refreshTokenService.verifyExpiration(refreshToken);
                })
                .map(RefreshToken::getUserInfo)
                .map(userInfo -> {
                    log.info("Generating new access token for user: {}", userInfo.getUsername());
                    String accessToken = jwtService.GenerateToken(userInfo.getUsername());
                    return JwtResponseDTO.builder()
                            .accessToken(accessToken)
                            .token(refreshTokenRequestDTO.getToken())
                            .build();
                })
                .orElseThrow(() -> {
                    log.warn("Refresh token not found in DB");
                    return new RuntimeException("Refresh Token is not in DB..!!");
                });
    }
}
