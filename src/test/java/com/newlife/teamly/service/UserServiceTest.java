package com.newlife.teamly.service;

import com.newlife.teamly.models.User;
import com.newlife.teamly.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ImageService imageService;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private String username = "testuser";

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username(username)
                .email("test@example.com")
                .profilePicture("old_url")
                .build();
    }

    @Test
    void shouldUpdateProfilePictureSuccessfully() throws IOException {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        String newImageUrl = "http://cloudinary.com/new_image.jpg";
        
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(testUser));
        when(imageService.upload(file)).thenReturn(Map.of("url", newImageUrl));
        
        // Act
        String resultUrl = userService.updateProfilePicture(username, file);
        
        // Assert
        assertEquals(newImageUrl, resultUrl);
        assertEquals(newImageUrl, testUser.getProfilePicture());
        verify(userRepository).save(testUser);
    }
}
