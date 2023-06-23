package com.sypulse.auth.infra;

import com.sypulse.auth.domain.AccountRepository;
import com.sypulse.auth.domain.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AccountRepository accountRepository;

    @Autowired
    public UserDetailsServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByAccount(username)
                .orElseThrow((() -> new UsernameNotFoundException("User not found")));

        return org.springframework.security.core.userdetails.User.builder()
                .username(account.getIdentityKey())
                .password(account.getPassword())
                .authorities("")
                .build();
    }
}