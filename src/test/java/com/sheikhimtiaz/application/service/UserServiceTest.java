package com.sheikhimtiaz.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserDetailsManager userDetailsManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        // Setup common mock behaviors here if any
    }

    @Test
    void registerNewUser_WhenUserExists_ThrowsException() {
        // Arrange
        String username = "existingUser";
        String password = "password";
        when(userDetailsManager.userExists(username)).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.registerNewUser(username, password));
    }

    @Test
    void registerNewUser_WhenUserDoesNotExist_CreatesUserSuccessfully() {
        // Arrange
        String username = "newUser";
        String password = "password";
        String encodedPassword = "encodedPassword";
        when(userDetailsManager.userExists(username)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        // Act
        userService.registerNewUser(username, password);

        // Assert
        verify(userDetailsManager).createUser(any());
    }
}