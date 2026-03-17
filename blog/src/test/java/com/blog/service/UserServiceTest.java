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
    void inscrire_hashesPassword_and_forcesLecteurRoleWhenAdminRequested() {
        User user = new User();
        user.setNom("Test User");
        user.setEmail("test@example.com");
        user.setPassword("secret123");
        user.setRole(Role.ADMIN); // Doit être ramené à LECTEUR

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("hashed");

        userService.inscrire(user);

        assertEquals(Role.LECTEUR, user.getRole());
        assertEquals("hashed", user.getPassword());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void getById_throwsResourceNotFoundWhenMissing() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getById(99L));
        verify(userRepository, times(1)).findById(99L);
    }
}

