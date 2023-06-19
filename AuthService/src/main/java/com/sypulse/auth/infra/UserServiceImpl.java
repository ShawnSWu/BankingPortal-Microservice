package com.sypulse.auth.infra;

import com.sypulse.auth.domain.BankUser;
import com.sypulse.auth.domain.UserRepository;
import com.sypulse.auth.domain.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails login(String account, String password) throws UsernameNotFoundException {
//        BankUser bankUser = userRepository.findByAccountAndPassword(account, password)
//                .orElseThrow(() -> {
//                    throw new UsernameNotFoundException("Log in failed with username: " + account);
//                });
        mockValidate(account, password);
        return new User(account, password, Collections.emptyList());
    }

    private void mockValidate(String account, String password){
        if (!StringUtils.equals(account,"Shawn") || !StringUtils.equals(password,"pwd")) {
            throw new UsernameNotFoundException("Log in failed with username: " + account);
        }
    }
}
