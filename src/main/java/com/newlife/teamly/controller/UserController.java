package com.newlife.teamly.controller;

import com.newlife.teamly.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PutMapping("/profile-picture")
    public ResponseEntity<String> updateProfilePicture(Authentication authentication, @RequestParam("file") MultipartFile file) throws IOException {
        String username = authentication.getName();
        String imageUrl = userService.updateProfilePicture(username, file);
        return ResponseEntity.ok(imageUrl);
    }
}
