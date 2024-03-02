package ju.example.training_management_system.service;

import ju.example.training_management_system.entity.NotificationEntity;
import ju.example.training_management_system.entity.users.CompanyEntity;
import ju.example.training_management_system.entity.users.StudentEntity;
import ju.example.training_management_system.entity.users.UserEntity;
import ju.example.training_management_system.exception.NotificationDoesNotExistException;
import ju.example.training_management_system.model.ApiResponse;
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
      NotificationEntity notificationEntity =
          notificationRepository
              .findById(notificationId)
              .orElseThrow(
                  () ->
                      new NotificationDoesNotExistException(
                          "Notification with id [" + notificationId + "] does not exist"));

      notificationRepository.delete(notificationEntity);
      return new ApiResponse(
          "Notification with id [" + notificationId + "] was marked as read successfully",
          HttpStatus.OK);
    } catch (NotificationDoesNotExistException ex) {
      return new ApiResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  public void notifyUser(String newStatus, String criteria, UserEntity user) {
    NotificationEntity notificationEntity = new NotificationEntity();
    if (user instanceof CompanyEntity company) {
      notificationEntity.setMessage(
          "Your AdvertisementEntity with job title ["
              + criteria
              + "] was "
              + newStatus.toLowerCase());
      notificationEntity.setUser(company);
    } else if (user instanceof StudentEntity student) {
      notificationEntity.setMessage(
          "Your Feedback with context [" + criteria + "] was " + newStatus.toLowerCase());
      notificationEntity.setUser(student);
    }
    notificationEntity.setUser(user);
    notificationRepository.save(notificationEntity);
  }
}
