package com.sypulse.auth.infra;

import com.sypulse.auth.domain.BankUser;
import com.sypulse.auth.domain.UserRepository;
import com.sypulse.auth.domain.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public BankUser login(String account, String password) throws UsernameNotFoundException {
//        BankUser bankUser = userRepository.findByAccountAndPassword(account, password)
//                .orElseThrow(() -> {
//                    throw new UsernameNotFoundException("Log in failed with username: " + account);
//                });
        //for test
        mockValidate(account, password);
        return BankUser.builder().id("P-0123456789").build();
    }

    private void mockValidate(String account, String password){
        if (!StringUtils.equals(account,"Shawn") || !StringUtils.equals(password,"pwd")) {
            throw new UsernameNotFoundException("Log in failed with username: " + account);
        }
    }
}
