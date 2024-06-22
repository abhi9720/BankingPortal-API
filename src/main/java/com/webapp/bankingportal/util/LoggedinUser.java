package com.webapp.bankingportal.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import com.webapp.bankingportal.exception.NotFoundException;

public class LoggedinUser {

    /**
     * Returns the account number of the currently logged in user. If there is
     * no user logged in, an exception is thrown.
     *
     * @return The account number of the currently logged in user.
     * @throws NotFoundException If there is no user logged in.
     */
    public static String getAccountNumber() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            User principal = (User) authentication.getPrincipal();
            return principal.getUsername();
        }
        throw new NotFoundException("No user is currently logged in.");
    }
}
