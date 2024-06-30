package com.webapp.bankingportal.service;

import com.webapp.bankingportal.entity.User;

public interface AuthService {
    public String generatePasswordResetToken(User user);

    public boolean verifyPasswordResetToken(String token, User user);

    public void deletePasswordResetToken(String token);
}
