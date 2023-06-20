package com.sypulse.auth.persentation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${x-api-key}")
    private String xApiKey;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/cp/error", "/api/v1/login").permitAll()
                .antMatchers("/**")
                .access("request.getHeader('x-api-key') != null && request.getHeader('x-api-key').equals('" + xApiKey + "')")
                .and()
                .httpBasic();
    }

}
