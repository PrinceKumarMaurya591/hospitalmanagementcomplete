package com.hospital.userservice.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hospital.userservice.exception.ResourceNotFoundException;
import com.hospital.userservice.model.Role;
import com.hospital.userservice.model.User;
import com.hospital.userservice.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID().toString())
                .username("testuser")
                .email("test@hospital.com")
                .password("encodedPassword")
                .roles(Set.of(Role.PATIENT))
                .enabled(true)
                .emailVerified(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        String userId = testUser.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        var result = userService.getUserById(userId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testUser.getId());
        assertThat(result.getEmail()).isEqualTo(testUser.getEmail());

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserById_ShouldThrowResourceNotFoundException_WhenUserNotFound() {
        String userId = "non-existent-id";
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with id: non-existent-id");

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void getUserByEmail_ShouldReturnUser_WhenUserExists() {
        String email = testUser.getEmail();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        var result = userService.getUserByEmail(email);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void getUserByEmail_ShouldThrowResourceNotFoundException_WhenUserNotFound() {
        String email = "nonexistent@email.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserByEmail(email))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with email: nonexistent@email.com");

        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        User user2 = User.builder()
                .id(UUID.randomUUID().toString())
                .username("user2")
                .email("user2@hospital.com")
                .password("encodedPassword")
                .roles(Set.of(Role.DOCTOR))
                .enabled(true)
                .emailVerified(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        List<User> users = Arrays.asList(testUser, user2);
        when(userRepository.findAll()).thenReturn(users);

        var result = userService.getAllUsers();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getEmail()).isEqualTo(testUser.getEmail());
        assertThat(result.get(1).getEmail()).isEqualTo(user2.getEmail());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void updateUser_ShouldUpdateUserSuccessfully() {
        String userId = testUser.getId();

        User userUpdates = User.builder()
                .username("updateduser")
                .email("updated@hospital.com")
                .enabled(false)
                .roles(Set.of(Role.ADMIN))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        var result = userService.updateUser(userId, userUpdates);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(testUser.getEmail());

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void deleteUser_ShouldDeleteUserSuccessfully() {
        String userId = testUser.getId();
        when(userRepository.existsById(userId)).thenReturn(true);

        userService.deleteUser(userId);

        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void deleteUser_ShouldThrowResourceNotFoundException_WhenUserNotFound() {
        String userId = "non-existent-id";
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found with id: non-existent-id");

        verify(userRepository, times(1)).existsById(userId);
        verify(userRepository, never()).deleteById(userId);
    }
}
