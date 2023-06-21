package com.sypulse.auth.persentation.controller;

import com.sypulse.auth.persentation.dto.AuthTokenRequest;
import com.sypulse.auth.persentation.dto.LoginRequest;
import com.sypulse.auth.persentation.dto.LoginResponse;
import com.sypulse.auth.usecase.UserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    public ResponseEntity<Authentication> verifyToken(@RequestBody AuthTokenRequest authTokenRequest) {
        String token = authTokenRequest.getToken();
        Authentication bankUser = userUseCase.verifyToken(token);
        return ResponseEntity.ok(bankUser);
    }

}
