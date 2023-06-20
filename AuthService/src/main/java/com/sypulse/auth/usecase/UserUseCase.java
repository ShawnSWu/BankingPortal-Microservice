package com.sypulse.auth.usecase;

import com.sypulse.auth.domain.BankUser;
import com.sypulse.auth.domain.JwtTokenService;
import com.sypulse.auth.domain.UserService;
import com.sypulse.auth.persentation.dto.LoginRequest;
import org.springframework.security.core.Authentication;
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
        BankUser bankUser = userService.login(loginRequest.getAccount(), loginRequest.getPassword());
        return jwtTokenService.generateToken(bankUser);
    }

    public Authentication verifyToken(String token) {
        return jwtTokenService.validateTokenSecret(token);
    }
}
