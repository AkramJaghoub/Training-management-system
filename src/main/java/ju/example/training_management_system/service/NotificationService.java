package ju.example.training_management_system.service;

import ju.example.training_management_system.exception.NotificationDoesNotExistException;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.model.company.advertisement.Notification;
import ju.example.training_management_system.model.users.Company;
import ju.example.training_management_system.model.users.Student;
import ju.example.training_management_system.model.users.User;
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
                    .orElseThrow(() -> new NotificationDoesNotExistException("Notification with id [" + notificationId + "] does not exist"));

            notificationRepository.delete(notification);
            return new ApiResponse("Notification with id [" + notificationId + "] was marked as read successfully", HttpStatus.OK);
        } catch (NotificationDoesNotExistException ex) {
            return new ApiResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public void notifyUser(String newStatus, String criteria, User user) {
        Notification notification = new Notification();
        if (user instanceof Company company) {
            notification.setMessage("Your Advertisement with job title [" + criteria + "] was "
                    + newStatus.toLowerCase());
            notification.setUser(company);
        } else if (user instanceof Student student){
            notification.setMessage("Your Feedback with context [" + criteria + "] was "
                    + newStatus.toLowerCase());
            notification.setUser(student);
        }
        notification.setUser(user);
        notificationRepository.save(notification);
    }
}