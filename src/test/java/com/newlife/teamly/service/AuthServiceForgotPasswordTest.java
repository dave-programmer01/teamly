package com.newlife.teamly.service;

import com.newlife.teamly.dto.ForgotPasswordRequest;
import com.newlife.teamly.dto.ResetPasswordRequest;
import com.newlife.teamly.models.User;
import com.newlife.teamly.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceForgotPasswordTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthService authService;

    @Test
    void forgotPassword_ShouldGenerateTokenAndSendEmail() {
        // Arrange
        String email = "test@example.com";
        User user = User.builder().email(email).build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        authService.forgotPassword(new ForgotPasswordRequest(email));

        // Assert
        assertNotNull(user.getResetToken());
        assertNotNull(user.getResetTokenExpiry());
        verify(userRepository).save(user);
        verify(emailService).sendEmail(eq(email), anyString(), anyString());
    }

    @Test
    void resetPassword_WithValidToken_ShouldChangePassword() {
        // Arrange
        String token = "valid-token";
        User user = User.builder()
                .resetToken(token)
                .resetTokenExpiry(LocalDateTime.now().plusHours(1))
                .build();
        when(userRepository.findByResetToken(token)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-password");

        // Act
        authService.resetPassword(new ResetPasswordRequest(token, "new-password"));

        // Assert
        assertEquals("encoded-password", user.getPassword());
        assertNull(user.getResetToken());
        assertNull(user.getResetTokenExpiry());
        verify(userRepository).save(user);
    }

    @Test
    void resetPassword_WithExpiredToken_ShouldThrowException() {
        // Arrange
        String token = "expired-token";
        User user = User.builder()
                .resetToken(token)
                .resetTokenExpiry(LocalDateTime.now().minusHours(1))
                .build();
        when(userRepository.findByResetToken(token)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            authService.resetPassword(new ResetPasswordRequest(token, "new-password"))
        );
        verify(userRepository, never()).save(any(User.class));
    }
}
