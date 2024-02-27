package ju.example.training_management_system.service;

import static ju.example.training_management_system.model.Role.STUDENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import ju.example.training_management_system.entity.users.UserEntity;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.repository.users.UserRepository;
import ju.example.training_management_system.util.PasswordHashingUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

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
    UserEntity user = getUser();
    String currentPassword = "test123";
    String newPassword = "new123";

    when(userRepository.findByEmail(email)).thenReturn(user);

    ApiResponse response =
        userService.changePassword(user.getEmail(), currentPassword, newPassword);

    verify(userRepository).save(user);

    assertEquals(HttpStatus.OK, response.getStatus());
  }

  @Test
  void invalid_change_password_when_currentPassword_doesnt_match() {
    UserEntity user = getUser();
    String currentPassword = "wrong password";
    String newPassword = "new123";

    when(userRepository.findByEmail(email)).thenReturn(user);

    ApiResponse response =
        userService.changePassword(user.getEmail(), currentPassword, newPassword);

    verify(userRepository, never()).save(any(UserEntity.class));

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
  }

  private UserEntity getUser() {
    return UserEntity.builder()
        .email("test123@gmail.com")
        .password(PasswordHashingUtil.hashPassword("test123"))
        .role(STUDENT)
        .joinDate(LocalDateTime.now())
        .build();
  }
}
