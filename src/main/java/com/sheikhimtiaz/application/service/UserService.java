package com.sheikhimtiaz.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserDetailsManager userDetailsManager;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserDetailsManager userDetailsManager,
                       PasswordEncoder passwordEncoder) {
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerNewUser(String username, String password) {
        if (userDetailsManager.userExists(username)) {
            throw new RuntimeException("Username already exists");
        }
        String encodedPassword = passwordEncoder.encode(password);
        User.UserBuilder userBuilder = User.withUsername(username)
                .password(encodedPassword)
                .roles("USER");
        userDetailsManager.createUser(userBuilder.build());
    }
}
