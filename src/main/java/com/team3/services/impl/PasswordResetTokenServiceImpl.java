package com.team3.services.impl;

import com.team3.entities.PasswordResetToken;
import com.team3.entities.User;
import com.team3.repositories.PasswordResetTokenRepository;
import com.team3.services.PasswordResetTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Override
    public void save(String token, User user) {

        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setUser(user);
        passwordResetToken.setExpiryDate(LocalDateTime.now());

        passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    public PasswordResetToken findByToken(String token) {

        return passwordResetTokenRepository.findByToken(token);
    }
}
