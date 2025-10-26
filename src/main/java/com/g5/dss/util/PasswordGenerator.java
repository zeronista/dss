package com.g5.dss.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility class để generate BCrypt password
 * Chạy class này để tạo password mã hóa
 */
public class PasswordGenerator {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // Password gốc
        String[] plainPasswords = {
            "admin123",
            "inventory123",
            "marketing123",
            "sales123",
            "staff123"
        };
        
        System.out.println("=== BCRYPT PASSWORD GENERATOR ===\n");
        
        for (String plainPassword : plainPasswords) {
            String encodedPassword = encoder.encode(plainPassword);
            System.out.println("Plain: " + plainPassword);
            System.out.println("BCrypt: " + encodedPassword);
            System.out.println();
        }
        
        // Verify password
        System.out.println("=== VERIFY PASSWORD ===\n");
        String testPassword = "admin123";
        String testHash = "$2a$10$t.lqgDcKq00oGYt1H0.sxePz2iTh3.1g9jigGILpPHv8w00MK5DqO";
        boolean matches = encoder.matches(testPassword, testHash);
        System.out.println("Password '" + testPassword + "' matches hash: " + matches);
    }
}
