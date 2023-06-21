package com.synpulse.api.presentation.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/swagger-ui/**", "/swagger-resources/**", "/v2/api-docs").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .cors()
                .and()
                .oauth2ResourceServer()
                .jwt();
    }
}