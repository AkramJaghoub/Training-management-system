package ju.example.training_management_system.service;

import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.model.users.User;
import ju.example.training_management_system.repository.users.UserRepository;
import ju.example.training_management_system.util.PasswordHashingUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static ju.example.training_management_system.model.users.Role.STUDENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private UserService userService;
    private String email;

    @BeforeEach
    void setUp() {
        email = "test123@gmail.com";
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    void valid_change_password() {
        User user = getUser();
        String currentPassword = "test123";
        String newPassword = "new123";

        when(userRepository.findByEmail(email)).thenReturn(user);

        ApiResponse response = userService.changePassword(user.getEmail(), currentPassword, newPassword);

        verify(userRepository).save(user);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void invalid_change_password_when_currentPassword_doesnt_match() {
        User user = getUser();
        String currentPassword = "wrong password";
        String newPassword = "new123";

        when(userRepository.findByEmail(email)).thenReturn(user);

        ApiResponse response = userService.changePassword(user.getEmail(), currentPassword, newPassword);

        verify(userRepository, never()).save(any(User.class));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
    }

    private User getUser() {
        return User.builder()
                .email("test123@gmail.com")
                .password(PasswordHashingUtil.hashPassword("test123"))
                .role(STUDENT)
                .joinDate(LocalDateTime.now())
                .build();
    }
}