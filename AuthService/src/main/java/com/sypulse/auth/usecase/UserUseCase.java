package com.sypulse.auth.usecase;

import com.sypulse.auth.domain.JwtTokenService;
import com.sypulse.auth.domain.UserService;
import com.sypulse.auth.persentation.dto.LoginRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class UserUseCase {

    private final JwtTokenService jwtTokenService;
    private final UserService userService;

    public UserUseCase(JwtTokenService jwtTokenService, UserService userService) {
        this.jwtTokenService = jwtTokenService;
        this.userService = userService;
    }

    public String login(LoginRequest loginRequest) {
        UserDetails userDetails = userService.login(loginRequest.getAccount(), loginRequest.getPassword());
        return jwtTokenService.generateToken(userDetails);
    }

    public void verifyToken(String token) {
        jwtTokenService.validateTokenSecret(token);
    }
}
