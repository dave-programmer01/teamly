package com.newlife.teamly.service;

import com.newlife.teamly.dto.*;
import com.newlife.teamly.jwt.JwtService;
import com.newlife.teamly.models.Roles;
import com.newlife.teamly.models.User;
import com.newlife.teamly.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public AuthResponse register(RegisterRequest request) {
        // Create new user with encoded password
        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Roles.USER)
                .profilePicture("https://i.pravatar.cc/300")
                .build();

        // Save to database

        userRepository.save(user);

        // Generate JWT for immediate login after registration
        var jwt = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(jwt)
                .role(user.getRole().name())
                .teamId(user.getTeam() == null ? null : user.getTeam().getTeamId())
                .profilePicture(user.getProfilePicture())
                .build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        String loginValue = request.getUsername() == null ? request.getEmail() : request.getUsername();
        // Let Spring Security validate credentials
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginValue,
                        request.getPassword()
                )
        );

        // If we get here, credentials are valid
        var user = userRepository.findByUsername(loginValue)
                .or(() -> userRepository.findByEmail(loginValue))
                .orElseThrow();

        // Generate and return JWT
        var jwt = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(jwt)
                .role(user.getRole().name())
                .teamId(user.getTeam() == null ? null : user.getTeam().getTeamId())
                .profilePicture(user.getProfilePicture())
                .build();
    }

    public void signOut() {
        // JWT is stateless; client-side token disposal is sufficient.
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow();
        userRepository.delete(user);
    }

    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with this email"));

        String otp = String.format("%06d", new java.util.Random().nextInt(1000000));
        user.setResetToken(otp);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        emailService.sendEmail(
                user.getEmail(),
                "Password Reset OTP",
                "Your password reset OTP is: " + otp + "\nThis OTP will expire in 1 hour."
        );
    }

    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid or expired token"));

        if (user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token has expired");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }


}
