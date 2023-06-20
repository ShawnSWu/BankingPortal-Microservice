package com.sypulse.auth.persentation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserTokenInfo {

    private String id;

    private String email;

    private String firstName;

    private String lastName;

    private Date iat;

    private Date expire;
}
