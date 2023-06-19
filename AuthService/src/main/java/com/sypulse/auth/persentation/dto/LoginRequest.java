package com.sypulse.auth.persentation.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoginRequest {
    private String account;
    private String password;
}
