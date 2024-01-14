package ju.example.training_management_system.service.student;

import static ju.example.training_management_system.util.Utils.*;

import jakarta.transaction.Transactional;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ju.example.training_management_system.dto.StudentInfoDto;
import ju.example.training_management_system.exception.UnauthorizedStudentAccessException;
import ju.example.training_management_system.exception.UserNotFoundException;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.model.PostStatus;
import ju.example.training_management_system.model.company.advertisement.Advertisement;
import ju.example.training_management_system.model.manage.student.StudentInfo;
import ju.example.training_management_system.model.users.Student;
import ju.example.training_management_system.model.users.User;
import ju.example.training_management_system.repository.AdvertisementRepository;
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

  public Student isUserAuthorizedAsStudent(User user, String email)
      throws UnauthorizedStudentAccessException {
    if (!(user instanceof Student student)) {
      throw new UnauthorizedStudentAccessException(
          "User with email " + email + " wasn't recognized as a student");
    }
    return student;
  }

  public ApiResponse setUpStudentDashboard(Model model, String email) {
    try {
      User existingUser = userRepository.findByEmail(email);
      if (existingUser == null) {
        throw new UserNotFoundException("User with email " + email + " wasn't found");
      }

      Student student = isUserAuthorizedAsStudent(existingUser, email);

      List<Advertisement> advertisements = getAdvertisementsPostedByLatestAndApproved();

      Map<Long, String> advertisementImages = getImages(advertisements);

      String studentName = getStudentFullName(student.getFirstName(), student.getLastName());
      String studentEducation = getStudentEducation(student.getMajor(), student.getUniversity());

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

  public Map<Long, String> getImages(List<Advertisement> advertisements){
    Map<Long, String> advertisementImages = new HashMap<>();
    for (Advertisement ad : advertisements) {
      advertisementImages.put(ad.getId(), ad.getImageUrl());
    }
    return advertisementImages;
  }

  private List<Advertisement> getAdvertisementsPostedByLatestAndApproved() {
    return advertisementRepository.findAll().stream()
        .sorted(Comparator.comparing(Advertisement::getPostDate).reversed())
        .filter(ad -> ad.getPostStatus().equals(PostStatus.APPROVED))
        .toList();
  }

  public ApiResponse setManageProfile(Model model, String email) {
    try {
      User existingUser = userRepository.findByEmail(email);
      if (existingUser == null) {
        throw new UserNotFoundException("User with email " + email + " wasn't found");
      }

      Student student = isUserAuthorizedAsStudent(existingUser, email);
      String studentEducation = getStudentEducation(student.getMajor(), student.getUniversity());


      String studentName = getStudentFullName(student.getFirstName(), student.getLastName());
      model.addAttribute("email", student.getEmail());
      model.addAttribute("studentName", studentName);
      model.addAttribute("university", student.getUniversity());
      model.addAttribute("major", student.getMajor());
      model.addAttribute("graduationYear", student.getGraduationYear());
      model.addAttribute("studentImage", student.getImageUrl());
      model.addAttribute("studentEducation", studentEducation);

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
      StudentInfo studentInfo = new StudentInfo().toEntity(infoDto);

      User existingUser = userRepository.findByEmail(email);
      if (existingUser == null) {
        throw new UserNotFoundException("User with email " + email + " wasn't found");
      }

      Student student = isUserAuthorizedAsStudent(existingUser, email);

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
