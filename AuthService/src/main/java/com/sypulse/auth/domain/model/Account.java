package com.sypulse.auth.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ACCOUNT")
public class Account {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "IDENTITY_KEY")
    private String identityKey;

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


    @Column(name = "ACCOUNT_CURRENCY")
    @OneToMany(targetEntity= CurrencyBalance.class, cascade=CascadeType.ALL)
    private List<CurrencyBalance> currencyAccounts;


}
