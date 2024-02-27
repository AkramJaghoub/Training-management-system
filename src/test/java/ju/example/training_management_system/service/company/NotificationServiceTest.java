package ju.example.training_management_system.service.company;

import static java.util.Optional.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import ju.example.training_management_system.entity.advertisement.NotificationEntity;
import ju.example.training_management_system.entity.users.UserEntity;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.repository.NotificationRepository;
import ju.example.training_management_system.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class NotificationServiceTest {

  private NotificationService notificationService;
  private NotificationRepository notificationRepository;

  @BeforeEach
  void setUp() {
    notificationRepository = mock(NotificationRepository.class);
    notificationService = new NotificationService(notificationRepository);
  }

  @Test
  void should_mark_notification_as_read() {
    long notificationId = 1L;
    NotificationEntity notificationEntity = getNotification();

    when(notificationRepository.findById(notificationId)).thenReturn(of(notificationEntity));

    ApiResponse response = notificationService.markNotificationAsRead(notificationId);

    verify(notificationRepository).delete(notificationEntity);

    assertEquals(HttpStatus.OK, response.getStatus());
  }

  @Test
  void invalid_read_when_id_is_not_found() {
    long notificationId = 999999L;
    NotificationEntity notification = getNotification();

    when(notificationRepository.findById(notificationId)).thenReturn(empty());

    ApiResponse response = notificationService.markNotificationAsRead(notificationId);

    verify(notificationRepository, never()).delete(notification);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
  }

  private NotificationEntity getNotification() {
    return NotificationEntity.builder()
        .id(1L)
        .message("testMessage")
        .user(new UserEntity())
        .build();
  }
}
