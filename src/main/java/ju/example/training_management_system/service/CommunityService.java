package ju.example.training_management_system.service;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static ju.example.training_management_system.model.PostStatus.APPROVED;
import static ju.example.training_management_system.service.student.StudentService.getStudentEducation;
import static ju.example.training_management_system.service.student.StudentService.getStudentFullName;
import static org.springframework.http.HttpStatus.*;

import java.util.*;
import ju.example.training_management_system.entity.FeedbackEntity;
import ju.example.training_management_system.entity.NotificationEntity;
import ju.example.training_management_system.entity.users.CompanyEntity;
import ju.example.training_management_system.entity.users.StudentEntity;
import ju.example.training_management_system.entity.users.UserEntity;
import ju.example.training_management_system.exception.UnauthorizedStudentAccessException;
import ju.example.training_management_system.exception.UserNotFoundException;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.repository.FeedbackRepository;
import ju.example.training_management_system.repository.NotificationRepository;
import ju.example.training_management_system.repository.users.CompanyRepository;
import ju.example.training_management_system.repository.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
@RequiredArgsConstructor
public class CommunityService {

  private final UserRepository userRepository;
  private final FeedbackRepository feedbackRepository;
  private final NotificationRepository notificationRepository;
  private final CompanyRepository companyRepository;

  public ApiResponse setUpCommunityPage(Model model, String email) {
    try {
      UserEntity user = userRepository.findByEmail(email);

      if (isNull(user)) {
        throw new UserNotFoundException("UserEntity with email " + email + " wasn't found");
      }

      if (!(user instanceof StudentEntity student)) {
        throw new UnauthorizedStudentAccessException(
            "UserEntity with email " + email + " wasn't recognized as a student");
      }

      List<FeedbackEntity> feedbackList = getFeedbacksByPostDateAndStatus();
      List<FeedbackEntity> studentFeedback = getFeedbacksByStudentIdAndPostDate(user.getId());

      String studentName = getStudentFullName(student.getFirstName(), student.getLastName());
      String studentEducation = getStudentEducation(student.getMajor(), student.getUniversity());

      Map<Long, String> allUserImages = getUserImages(feedbackList);
      List<NotificationEntity> notifications = notificationRepository.findByUser(student);
      Collections.reverse(notifications);

      List<String> companyNames = getCompanyNames();

      model.addAttribute("companyNames", companyNames);
      model.addAttribute("studentName", studentName);
      model.addAttribute("studentId", user.getId());
      model.addAttribute("studentImage", getUserImage(user).orElse(null));
      model.addAttribute("feedbackList", feedbackList);
      model.addAttribute("studentFeedback", studentFeedback);
      model.addAttribute("allUserImages", allUserImages);
      model.addAttribute("studentEducation", studentEducation);
      model.addAttribute("notifications", notifications);

      return new ApiResponse("Set up was correctly done", OK);
    } catch (UserNotFoundException | UnauthorizedStudentAccessException ex) {
      return new ApiResponse(
          ex.getMessage(), ex instanceof UserNotFoundException ? BAD_REQUEST : UNAUTHORIZED);
    }
  }

  private List<String> getCompanyNames() {
    return companyRepository.findAll().stream().map(CompanyEntity::getCompanyName).toList();
  }

  private Map<Long, String> getUserImages(List<FeedbackEntity> feedbackList) {
    Map<Long, String> allUserImages = new HashMap<>();

    for (FeedbackEntity feedback : feedbackList) {
      UserEntity user = feedback.getStudent();
      allUserImages.put(user.getId(), getUserImage(user).orElse(null));
    }

    return allUserImages;
  }

  private Optional<String> getUserImage(UserEntity user) {
    if (nonNull(user.getImageUrl())) {
      return Optional.of(user.getImageUrl());
    }
    return Optional.empty();
  }

  private List<FeedbackEntity> getFeedbacksByStudentIdAndPostDate(Long studentId) {
    return feedbackRepository.findByStudent_Id(studentId).stream()
        .sorted(Comparator.comparing(FeedbackEntity::getPostDate).reversed())
        .toList();
  }

  private List<FeedbackEntity> getFeedbacksByPostDateAndStatus() {
    return feedbackRepository.findAll().stream()
        .sorted(Comparator.comparing(FeedbackEntity::getPostDate).reversed())
        .filter((feedback) -> feedback.getStatus().equals(APPROVED))
        .toList();
  }
}
