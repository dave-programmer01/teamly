package com.newlife.teamly.service;

import com.newlife.teamly.dto.AuthRequest;
import com.newlife.teamly.dto.AuthResponse;
import com.newlife.teamly.dto.RegisterRequest;
import com.newlife.teamly.jwt.JwtService;
import com.newlife.teamly.models.Roles;
import com.newlife.teamly.models.User;
import com.newlife.teamly.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

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
                .build();
    }

    public void signOut() {
        // JWT is stateless; client-side token disposal is sufficient.
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow();
        userRepository.delete(user);
    }


}
