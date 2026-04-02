package com.newlife.teamly.service;

import com.newlife.teamly.models.User;
import com.newlife.teamly.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ImageService imageService;

    public String updateProfilePicture(String username, MultipartFile file) throws IOException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Map uploadResult = imageService.upload(file);
        String imageUrl = (String) uploadResult.get("url");

        user.setProfilePicture(imageUrl);
        userRepository.save(user);

        return imageUrl;
    }
}
