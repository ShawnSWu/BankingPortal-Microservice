package com.synpulse.api.presentation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transaction/v1")
public class TransactionController {

    @GetMapping("/{id}")
    public String getMoneyTransaction(@PathVariable Long id) {

        //do something

        return "User with ID: " + id;
    }

}
