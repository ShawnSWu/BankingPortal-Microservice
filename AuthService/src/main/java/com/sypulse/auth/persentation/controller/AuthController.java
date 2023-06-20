package com.sypulse.auth.persentation.controller;

import com.sypulse.auth.persentation.dto.AuthTokenRequest;
import com.sypulse.auth.persentation.dto.LoginRequest;
import com.sypulse.auth.persentation.dto.LoginResponse;
import com.sypulse.auth.persentation.dto.UserTokenInfo;
import com.sypulse.auth.usecase.UserUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/v1")
public class AuthController {

    private final UserUseCase userUseCase;

    public AuthController(UserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        String token = userUseCase.login(loginRequest);
        return ResponseEntity.ok(LoginResponse.builder().token(token).build());
    }

    @PostMapping("/verify")
    public ResponseEntity<UserTokenInfo> verifyToken(@RequestBody AuthTokenRequest authTokenRequest) {
        String token = authTokenRequest.getToken();
        UserTokenInfo bankUser = userUseCase.verifyToken(token);
        return ResponseEntity.ok(bankUser);
    }

}
