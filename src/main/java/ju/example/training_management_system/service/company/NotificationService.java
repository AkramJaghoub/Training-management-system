package ju.example.training_management_system.service.company;

import ju.example.training_management_system.exception.NotificationDoesNotExistException;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.model.company.advertisement.Notification;
import ju.example.training_management_system.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public ApiResponse markNotificationAsRead(long notificationId) {
        try {
            Notification notification = notificationRepository.findById(notificationId)
                    .orElseThrow(() -> new NotificationDoesNotExistException("Notification with id ]" + notificationId + "] does not exist"));

            notificationRepository.delete(notification);
            return new ApiResponse("Notification with id ]" + notificationId + "] was marked as read successfully", HttpStatus.OK);
        } catch (NotificationDoesNotExistException ex) {
            return new ApiResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}