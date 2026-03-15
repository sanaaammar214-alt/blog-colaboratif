package com.blog.service;

import com.blog.model.Role;
import com.blog.model.User;
import com.blog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void inscrire_validUser_savesWithHashedPassword() {
        User user = new User();
        user.setNom("Alice");
        user.setEmail("alice@test.com");
        user.setPassword("password123");

        when(userRepository.existsByEmail("alice@test.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed_password");

        userService.inscrire(user);

        assertEquals("hashed_password", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void inscrire_duplicateEmail_throwsException() {
        User user = new User();
        user.setNom("Bob");
        user.setEmail("bob@test.com");
        user.setPassword("password123");

        when(userRepository.existsByEmail("bob@test.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.inscrire(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    void inscrire_shortPassword_throwsException() {
        User user = new User();
        user.setNom("Charlie");
        user.setEmail("charlie@test.com");
        user.setPassword("abc");

        when(userRepository.existsByEmail("charlie@test.com")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> userService.inscrire(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    void inscrire_adminRoleAttempt_forcedToLecteur() {
        User user = new User();
        user.setNom("Hacker");
        user.setEmail("hacker@test.com");
        user.setPassword("password123");
        user.setRole(Role.ADMIN);

        when(userRepository.existsByEmail("hacker@test.com")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashed");

        userService.inscrire(user);

        assertEquals(Role.LECTEUR, user.getRole());
    }

    @Test
    void changerRole_existingUser_updatesRole() {
        User user = new User();
        user.setId(1L);
        user.setRole(Role.LECTEUR);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.changerRole(1L, Role.AUTEUR);

        assertEquals(Role.AUTEUR, user.getRole());
        verify(userRepository).save(user);
    }

    @Test
    void changerRole_unknownUser_throwsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> userService.changerRole(99L, Role.AUTEUR));
    }
}
