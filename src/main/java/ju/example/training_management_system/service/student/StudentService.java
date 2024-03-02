package ju.example.training_management_system.service.student;

import static ju.example.training_management_system.util.Utils.*;

import jakarta.transaction.Transactional;
import java.util.*;
import ju.example.training_management_system.dto.StudentInfoDto;
import ju.example.training_management_system.entity.AdvertisementEntity;
import ju.example.training_management_system.entity.NotificationEntity;
import ju.example.training_management_system.entity.users.StudentEntity;
import ju.example.training_management_system.entity.users.UserEntity;
import ju.example.training_management_system.exception.UnauthorizedStudentAccessException;
import ju.example.training_management_system.exception.UserNotFoundException;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.model.PostStatus;
import ju.example.training_management_system.model.StudentInfo;
import ju.example.training_management_system.repository.AdvertisementRepository;
import ju.example.training_management_system.repository.NotificationRepository;
import ju.example.training_management_system.repository.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class StudentService {

  private final UserRepository userRepository;
  private final AdvertisementRepository advertisementRepository;
  private final NotificationRepository notificationRepository;

  public static String getStudentFullName(String firstName, String lastName) {
    return firstName + " " + lastName;
  }

  public static String getStudentEducation(String studentMajor, String studentUniversity) {
    StringBuilder educationBuilder = new StringBuilder();

    if (studentMajor != null) {
      educationBuilder.append(studentMajor);
    }

    if (studentUniversity != null) {
      if (!educationBuilder.isEmpty()) {
        educationBuilder.append(", ");
      }
      educationBuilder.append(studentUniversity);
    }
    return educationBuilder.toString();
  }

  public StudentEntity isUserAuthorizedAsStudent(UserEntity user, String email)
      throws UnauthorizedStudentAccessException {
    if (!(user instanceof StudentEntity student)) {
      throw new UnauthorizedStudentAccessException(
          "UserEntity with email " + email + " wasn't recognized as a student");
    }
    return student;
  }

  public ApiResponse setUpStudentDashboard(Model model, String email) {
    try {
      UserEntity existingUser = userRepository.findByEmail(email);
      if (existingUser == null) {
        throw new UserNotFoundException("UserEntity with email " + email + " wasn't found");
      }

      StudentEntity student = isUserAuthorizedAsStudent(existingUser, email);

      List<AdvertisementEntity> advertisements = getAdvertisementsPostedByLatestAndApproved();

      List<NotificationEntity> notifications = notificationRepository.findByUser(student);
      Collections.reverse(notifications);

      Map<Long, String> advertisementImages = getImages(advertisements);

      String studentName = getStudentFullName(student.getFirstName(), student.getLastName());
      String studentEducation = getStudentEducation(student.getMajor(), student.getUniversity());

      model.addAttribute("notifications", notifications);
      model.addAttribute("studentImage", student.getImageUrl());
      model.addAttribute("advertisements", advertisements);
      model.addAttribute("studentName", studentName);
      model.addAttribute("advertisementImages", advertisementImages);
      model.addAttribute("studentEducation", studentEducation);

      return new ApiResponse("Set up was correctly done", HttpStatus.OK);
    } catch (UserNotFoundException ex) {
      return new ApiResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (UnauthorizedStudentAccessException ex) {
      return new ApiResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }
  }

  public Map<Long, String> getImages(List<AdvertisementEntity> advertisements) {
    Map<Long, String> advertisementImages = new HashMap<>();
    for (AdvertisementEntity ad : advertisements) {
      advertisementImages.put(ad.getId(), ad.getImageUrl());
    }
    return advertisementImages;
  }

  private List<AdvertisementEntity> getAdvertisementsPostedByLatestAndApproved() {
    return advertisementRepository.findAll().stream()
        .sorted(Comparator.comparing(AdvertisementEntity::getPostDate).reversed())
        .filter(ad -> ad.getPostStatus().equals(PostStatus.APPROVED))
        .toList();
  }

  public ApiResponse setManageProfile(Model model, String email) {
    try {
      UserEntity existingUser = userRepository.findByEmail(email);
      if (existingUser == null) {
        throw new UserNotFoundException("UserEntity with email " + email + " wasn't found");
      }

      StudentEntity student = isUserAuthorizedAsStudent(existingUser, email);
      String studentEducation = getStudentEducation(student.getMajor(), student.getUniversity());
      List<NotificationEntity> notifications = notificationRepository.findByUser(student);
      Collections.reverse(notifications);

      String studentName = getStudentFullName(student.getFirstName(), student.getLastName());
      model.addAttribute("email", student.getEmail());
      model.addAttribute("studentName", studentName);
      model.addAttribute("university", student.getUniversity());
      model.addAttribute("major", student.getMajor());
      model.addAttribute("graduationYear", student.getGraduationYear());
      model.addAttribute("studentImage", student.getImageUrl());
      model.addAttribute("studentEducation", studentEducation);
      model.addAttribute("notifications", notifications);

      return new ApiResponse("Set up was correctly done", HttpStatus.OK);
    } catch (UserNotFoundException ex) {
      return new ApiResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (UnauthorizedStudentAccessException ex) {
      return new ApiResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }
  }

  @Transactional
  public ApiResponse updateStudentDetails(@RequestBody StudentInfoDto infoDto, String email) {
    try {
      StudentInfo studentInfo = new StudentInfo().toModel(infoDto);

      UserEntity existingUser = userRepository.findByEmail(email);
      if (existingUser == null) {
        throw new UserNotFoundException("UserEntity with email " + email + " wasn't found");
      }

      StudentEntity student = isUserAuthorizedAsStudent(existingUser, email);

      boolean isChanged = false;

      if (studentInfo.getFirstName() != null) {
        student.setFirstName(studentInfo.getFirstName());
        isChanged = true;
      }

      if (studentInfo.getLastName() != null) {
        student.setLastName(studentInfo.getLastName());
        isChanged = true;
      }

      if (infoDto.getStudentImage() != null) {
        String imageUrl = saveImage(infoDto.getStudentImage(), String.valueOf(student.getId()));
        student.setImageUrl(imageUrl);
        isChanged = true;
      }

      if (studentInfo.getGraduationYear() != null) {
        student.setGraduationYear(studentInfo.getGraduationYear());
        isChanged = true;
      }

      if (studentInfo.getUniversity() != null) {
        student.setUniversity(studentInfo.getUniversity());
        isChanged = true;
      }

      if (studentInfo.getMajor() != null) {
        student.setMajor(studentInfo.getMajor());
        isChanged = true;
      }

      if (!isChanged) {
        throw new UnsupportedOperationException(
            "You can't proceed with this operation, you have to at least change one field");
      }

      userRepository.save(student);
      return new ApiResponse("Student details updated successfully", HttpStatus.OK);
    } catch (UserNotFoundException | UnsupportedOperationException ex) {
      return new ApiResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (UnauthorizedStudentAccessException ex) {
      return new ApiResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }
  }
}
