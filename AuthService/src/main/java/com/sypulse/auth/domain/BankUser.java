package com.sypulse.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "BANK_USER")
public class BankUser {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "ACCOUNT")
    private String account;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "LAST_LOGIN_AT")
    private Date lastLoginAt;

    @Column(name = "LAST_PASSWORD_UPDATE_AT")
    private Date lastPasswordUpdateAt;

}
