package ju.example.training_management_system.service.student;

import jakarta.transaction.Transactional;
import ju.example.training_management_system.dto.StudentInfoDto;
import ju.example.training_management_system.exception.UnauthorizedStudentAccessException;
import ju.example.training_management_system.exception.UserNotFoundException;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.model.company.advertisement.AdStatus;
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

import java.util.Comparator;
import java.util.List;

import static ju.example.training_management_system.util.Utils.*;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final UserRepository userRepository;
    private final AdvertisementRepository advertisementRepository;

    public static String getStudentFullName(String firstName, String lastName) {
        return firstName + " " + lastName;
    }

    public Student isUserAuthorizedAsStudent(User user, String email) throws UnauthorizedStudentAccessException {
        if (!(user instanceof Student student)) {
            throw new UnauthorizedStudentAccessException("User with email " + email + " wasn't recognized as a student");
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

            String base64Image = null;
            if (student.getImage() != null) {
                byte[] decompressedImage = decompressImage(student.getImage());
                base64Image = convertToBase64(decompressedImage);
            }

            List<Advertisement> advertisements = getAdvertisementsPostedByLatestAndApproved();
            String studentName = getStudentFullName(student.getFirstName(), student.getLastName());

            model.addAttribute("studentImage", base64Image);
            model.addAttribute("advertisements", advertisements);
            model.addAttribute("studentName", studentName);

            return new ApiResponse("Set up was correctly done", HttpStatus.OK);
        } catch (UserNotFoundException ex) {
            return new ApiResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (UnauthorizedStudentAccessException ex) {
            return new ApiResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    private List<Advertisement> getAdvertisementsPostedByLatestAndApproved() {
        return advertisementRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Advertisement::getPostDate).reversed())
                .filter(ad -> ad.getAdStatus().equals(AdStatus.APPROVED))
                .toList();
    }

    public ApiResponse setManageProfile(Model model, String email) {
        try {
            User existingUser = userRepository.findByEmail(email);
            if (existingUser == null) {
                throw new UserNotFoundException("User with email " + email + " wasn't found");
            }

            Student student = isUserAuthorizedAsStudent(existingUser, email);

            String base64Image = null;
            if (student.getImage() != null) {
                byte[] decompressedImage = decompressImage(student.getImage());
                base64Image = convertToBase64(decompressedImage);
            }

            String studentName = student.getFirstName() + " " + student.getLastName();

            model.addAttribute("email", student.getEmail());
            model.addAttribute("studentName", studentName);
            model.addAttribute("university", student.getUniversity());
            model.addAttribute("major", student.getMajor());
            model.addAttribute("graduationYear", student.getGraduationYear());
            model.addAttribute("studentImage", base64Image);

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
                byte[] imageBytes = saveImage(infoDto.getStudentImage());
                student.setImage(imageBytes);
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

            if(!isChanged){
                throw new UnsupportedOperationException("You can't proceed with this operation, you have to at least change one field");
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
