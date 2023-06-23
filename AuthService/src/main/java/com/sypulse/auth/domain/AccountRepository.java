package com.sypulse.auth.domain;

import com.sypulse.auth.domain.model.Account;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account, Long> {

    Optional<Account> findByAccount(String account);
}
