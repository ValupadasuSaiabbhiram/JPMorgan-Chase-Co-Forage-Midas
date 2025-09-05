package com.jpmc.midascore.web;

import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BalanceController {

    private final UserRepository userRepository;

    public BalanceController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * GET /balance?userId=123
     * Returns Balance JSON. If user not found, returns 0.
     */
    @GetMapping("/balance")
    public Balance getBalance(@RequestParam("userId") long userId) {
        UserRecord user = userRepository.findById(userId);
        float amount = (user != null) ? user.getBalance() : 0f;
        return new Balance(amount);
    }
}
