package com.sypulse.auth.domain;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService {

    BankUser login(String account, String password) throws UsernameNotFoundException;
}
