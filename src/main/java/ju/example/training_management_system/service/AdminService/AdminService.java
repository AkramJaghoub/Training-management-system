package ju.example.training_management_system.service.AdminService;

import static java.util.Objects.nonNull;
import static ju.example.training_management_system.model.PostStatus.APPROVED;
import static ju.example.training_management_system.model.PostStatus.PENDING;
import static ju.example.training_management_system.util.Utils.isEmpty;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import ju.example.training_management_system.entity.FeedbackEntity;
import ju.example.training_management_system.entity.advertisement.AdvertisementEntity;
import ju.example.training_management_system.entity.users.StudentEntity;
import ju.example.training_management_system.entity.users.UserEntity;
import ju.example.training_management_system.exception.AdDoesNotExistException;
import ju.example.training_management_system.exception.FeedbackDoesNotExistException;
import ju.example.training_management_system.exception.UserNotFoundException;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.model.PostStatus;
import ju.example.training_management_system.model.Role;
import ju.example.training_management_system.repository.AdvertisementRepository;
import ju.example.training_management_system.repository.FeedbackRepository;
import ju.example.training_management_system.repository.users.CompanyRepository;
import ju.example.training_management_system.repository.users.StudentRepository;
import ju.example.training_management_system.repository.users.UserRepository;
import ju.example.training_management_system.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
@RequiredArgsConstructor
public class AdminService {

  private final UserRepository userRepository;
  private final AdvertisementRepository advertisementRepository;
  private final StudentRepository studentRepository;
  private final CompanyRepository companyRepository;
  private final NotificationService notificationService;
  private final FeedbackRepository feedbackRepository;

  public void setUpAdminDashboardPage(Model model) {
    setUpFields(model);
  }

  public void setUpUserListPage(Model model) {
    setUpFields(model);
  }

  private void setUpFields(Model model) {
    List<UserEntity> users = getUsersByJoinDate();

    List<AdvertisementEntity> advertisements = advertisementRepository.findAll();

    Map<Long, String> userImages = fetchUserImages(users);
    List<UserEntity> newUsers = getNewUsers(users);

    long numOfCompanies = users.stream().filter(user -> user.getRole() == Role.COMPANY).count();

    long numOfStudents = users.stream().filter(user -> user.getRole() == Role.STUDENT).count();

    long newAdsCount =
        advertisements.stream()
            .filter(
                ad ->
                    ad.getPostDate() != null
                        && ChronoUnit.HOURS.between(ad.getPostDate(), LocalDateTime.now()) < 24)
            .count();

    model.addAttribute("usersCount", users.size());
    model.addAttribute("companiesCount", numOfCompanies);
    model.addAttribute("studentsCount", numOfStudents);
    model.addAttribute("advertisementsCount", advertisements.size());
    model.addAttribute("newUsers", newUsers);
    model.addAttribute("users", users);
    model.addAttribute("userImages", userImages);
    model.addAttribute("newUsersCount", newUsers.size());
    model.addAttribute("newAdvertisementsCount", newAdsCount);
  }

  private Map<Long, String> fetchUserImages(List<UserEntity> users) {
    Map<Long, String> userImages = new HashMap<>();
    users.forEach(
        user -> getUserImage(user).ifPresent(imageUrl -> userImages.put(user.getId(), imageUrl)));
    return userImages;
  }

  private List<UserEntity> getUsersByJoinDate() {
    return userRepository.findAll().stream()
        .sorted(Comparator.comparing(UserEntity::getJoinDate).reversed())
        .collect(Collectors.toList());
  }

  private Optional<String> getUserImage(UserEntity user) {
    if (nonNull(user.getImageUrl())) {
      return Optional.of(user.getImageUrl());
    }
    return Optional.empty();
  }

  private List<UserEntity> getNewUsers(List<UserEntity> users) {
    return users.stream()
        .filter(
            user ->
                nonNull(user.getJoinDate())
                    && ChronoUnit.HOURS.between(user.getJoinDate(), LocalDateTime.now()) < 24)
        .toList();
  }

  public void setUpAdsListPage(Model model) {
    List<AdvertisementEntity> advertisements = getAdvertisementsByPostDate();
    model.addAttribute("advertisements", advertisements);
  }

  public List<AdvertisementEntity> getAdvertisementsByPostDate() {
    return advertisementRepository.findAll().stream()
        .sorted(Comparator.comparing(AdvertisementEntity::getPostDate).reversed())
        .toList();
  }

  public void setUpStudentsFeedbackPage(Model model) {
    List<FeedbackEntity> feedbackList = getFeedbackListByPostDate();

    Map<Long, String> studentImages = new HashMap<>();
    for (FeedbackEntity feedback : feedbackList) {
      StudentEntity student = feedback.getStudent();
      if (nonNull(student)) {
        getUserImage(student).ifPresent(imageUrl -> studentImages.put(student.getId(), imageUrl));
      }
    }

    model.addAttribute("feedbackList", feedbackList);
    model.addAttribute("studentImages", studentImages);
  }

  private List<FeedbackEntity> getFeedbackListByPostDate() {
    return feedbackRepository.findAll().stream()
        .sorted(Comparator.comparing(FeedbackEntity::getPostDate).reversed())
        .toList();
  }

  @Transactional
  public ApiResponse deleteUser(long userId) {
    try {
      Optional<UserEntity> usersOpt = userRepository.findById(userId);
      if (usersOpt.isEmpty()) {
        throw new UserNotFoundException("UserEntity with id [" + userId + "] was not found");
      }

      UserEntity user = usersOpt.get();

      if (user.getRole() == Role.STUDENT) {
        studentRepository.deleteById(userId);
      } else if (user.getRole() == Role.COMPANY) {
        advertisementRepository.deleteByCompanyId(userId);
        companyRepository.deleteById(userId);
      }

      userRepository.deleteById(userId);
      return new ApiResponse(
          "UserEntity with id [" + userId + "] was deleted successfully", HttpStatus.OK);

    } catch (UserNotFoundException ex) {
      return new ApiResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  public ApiResponse updateAdStatus(long adId, String newStatus) {
    try {
      AdvertisementEntity ad =
          advertisementRepository
              .findById(adId)
              .orElseThrow(
                  () ->
                      new AdDoesNotExistException(
                          "AdvertisementEntity with [" + adId + "] was not found"));

      if (isEmpty(newStatus)) {
        ad.setPostStatus(PENDING);
      } else {
        ad.setPostStatus(PostStatus.valueOf(newStatus));
        // notify the company
        notificationService.notifyUser(newStatus, ad.getJobTitle(), ad.getCompany());
      }

      advertisementRepository.save(ad);
      return new ApiResponse(
          "AdvertisementEntity with ["
              + adId
              + "] and name ["
              + ad.getJobTitle()
              + "] successfully got "
              + (newStatus.equals(APPROVED.name()) ? "approved" : "rejected"),
          HttpStatus.OK);
    } catch (AdDoesNotExistException ex) {
      return new ApiResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }

  public ApiResponse updateFeedbackStatus(long feedbackId, String newStatus) {
    try {
      FeedbackEntity feedback =
          feedbackRepository
              .findById(feedbackId)
              .orElseThrow(
                  () ->
                      new FeedbackDoesNotExistException(
                          "AdvertisementEntity with [" + feedbackId + "] was not found"));

      if (!isEmpty(newStatus)) {
        feedback.setStatus(PostStatus.valueOf(newStatus));
        // notify the student
        String criteria =
            "company: "
                + feedback.getCompany().getCompanyName()
                + " and rating of ["
                + feedback.getRating()
                + "]";
        notificationService.notifyUser(newStatus, criteria, feedback.getStudent());
      } else {
        feedback.setStatus(PENDING);
      }

      feedbackRepository.save(feedback);
      return new ApiResponse(
          "Feedback with ["
              + feedbackId
              + "] successfully got "
              + (newStatus.equals(APPROVED.name()) ? "approved" : "rejected"),
          HttpStatus.OK);
    } catch (FeedbackDoesNotExistException ex) {
      return new ApiResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }
}
