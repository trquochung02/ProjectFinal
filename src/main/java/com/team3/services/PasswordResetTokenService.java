package com.team3.services;

import com.team3.entities.PasswordResetToken;
import com.team3.entities.User;

public interface PasswordResetTokenService {

    void save(String token, User user);

    PasswordResetToken findByToken(String token);
}
