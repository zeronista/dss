package com.g5.dss.service;

import com.g5.dss.domain.User;
import com.g5.dss.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User createUser(String username, String password, String email, String role) {
        User user = new User(username, passwordEncoder.encode(password), email, role);
        return userRepository.save(user);
    }

    public boolean authenticate(String username, String password) {
        Optional<User> user = findByUsername(username);
        return user.isPresent() && passwordEncoder.matches(password, user.get().getPassword());
    }
}

