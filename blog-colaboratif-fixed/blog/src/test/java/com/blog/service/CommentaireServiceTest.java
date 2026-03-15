package com.blog.service;

import com.blog.model.Commentaire;
import com.blog.model.Role;
import com.blog.model.User;
import com.blog.repository.CommentaireRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentaireServiceTest {

    @Mock
    private CommentaireRepository commentaireRepository;

    @InjectMocks
    private CommentaireService commentaireService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private User makeUser(Long id, Role role) {
        User u = new User();
        u.setId(id);
        u.setRole(role);
        return u;
    }

    @Test
    void delete_byOwner_succeeds() {
        User owner = makeUser(1L, Role.LECTEUR);
        Commentaire c = new Commentaire();
        c.setUser(owner);

        when(commentaireRepository.findById(10L)).thenReturn(Optional.of(c));

        assertDoesNotThrow(() -> commentaireService.delete(10L, owner));
        verify(commentaireRepository).deleteById(10L);
    }

    @Test
    void delete_byAdmin_succeeds() {
        User owner = makeUser(1L, Role.AUTEUR);
        User admin = makeUser(2L, Role.ADMIN);
        Commentaire c = new Commentaire();
        c.setUser(owner);

        when(commentaireRepository.findById(10L)).thenReturn(Optional.of(c));

        assertDoesNotThrow(() -> commentaireService.delete(10L, admin));
        verify(commentaireRepository).deleteById(10L);
    }

    @Test
    void delete_byOtherUser_throwsSecurityException() {
        User owner = makeUser(1L, Role.AUTEUR);
        User other = makeUser(2L, Role.LECTEUR);
        Commentaire c = new Commentaire();
        c.setUser(owner);

        when(commentaireRepository.findById(10L)).thenReturn(Optional.of(c));

        assertThrows(SecurityException.class, () -> commentaireService.delete(10L, other));
        verify(commentaireRepository, never()).deleteById(any());
    }

    @Test
    void delete_notFound_throwsRuntimeException() {
        User user = makeUser(1L, Role.LECTEUR);
        when(commentaireRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> commentaireService.delete(99L, user));
    }
}
