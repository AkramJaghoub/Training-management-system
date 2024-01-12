package ju.example.training_management_system.service.company;

import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.model.company.advertisement.Notification;
import ju.example.training_management_system.model.users.User;
import ju.example.training_management_system.repository.NotificationRepository;
import ju.example.training_management_system.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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
        Notification notification = getNotification();

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.ofNullable(notification));

        ApiResponse response = notificationService.markNotificationAsRead(notificationId);

        verify(notificationRepository).delete(notification);

        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    void invalid_read_when_id_is_not_found() {
        long notificationId = 999999L;
        Notification notification = getNotification();

        when(notificationRepository.findById(notificationId)).thenReturn(Optional.empty());

        ApiResponse response = notificationService.markNotificationAsRead(notificationId);

        verify(notificationRepository, never()).delete(notification);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
    }

    private Notification getNotification() {
        return Notification.builder()
                .id(1L)
                .message("test")
                .user(new User())
                .build();
    }
}