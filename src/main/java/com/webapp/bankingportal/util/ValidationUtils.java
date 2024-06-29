package com.webapp.bankingportal.util;

public class ValidationUtils {
    public static boolean isValidEmail(String identifier) {
        // Basic email validation (add more robust validation if needed)
        return identifier != null && identifier.contains("@");
    }

    public static boolean isValidAccountNumber(String identifier) {
        // Account number validation logic (e.g., length check)
        return identifier != null && identifier.length() == 6;
    }

}
