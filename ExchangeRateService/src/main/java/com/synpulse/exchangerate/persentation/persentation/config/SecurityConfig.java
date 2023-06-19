package com.synpulse.exchangerate.persentation.persentation.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${exchangetate.x-api-key}")
    private String xApiKey;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/webjars/**", "/swagger-ui/**").permitAll()
                .and()
                .addFilterBefore(new TokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    private class TokenFilter extends OncePerRequestFilter {

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {

            String userXApiKey = request.getHeader("x-api-key");

            if (StringUtils.isEmpty(userXApiKey)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Missing x-api-key");
                return;
            }

            if (!StringUtils.equalsIgnoreCase(xApiKey, userXApiKey)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid x-api-key");
                return;
            }
            filterChain.doFilter(request, response);
        }
    }
}
