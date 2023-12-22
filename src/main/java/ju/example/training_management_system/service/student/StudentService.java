package ju.example.training_management_system.service.student;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import ju.example.training_management_system.dto.CompanyInfoDto;
import ju.example.training_management_system.dto.StudentInfoDto;
import ju.example.training_management_system.exception.UnauthorizedCompanyAccessException;
import ju.example.training_management_system.exception.UnauthorizedStudentAccessException;
import ju.example.training_management_system.exception.UserNotFoundException;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.model.manage.company.CompanyInfo;
import ju.example.training_management_system.model.manage.student.StudentInfo;
import ju.example.training_management_system.model.users.Company;
import ju.example.training_management_system.model.users.Student;
import ju.example.training_management_system.model.users.User;
import ju.example.training_management_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static ju.example.training_management_system.util.Utils.*;

@Service
@RequiredArgsConstructor
public class StudentService {
    private final UserRepository userRepository;

    public void setUpStudentDashboard(Model model, String email, HttpServletResponse response) {
        User existingUser = userRepository.findByEmail(email);
        if (existingUser == null) {
            throw new UserNotFoundException("User with email " + email + " wasn't found");
        }

        if (!(existingUser instanceof Student student)) {
            throw new UnauthorizedStudentAccessException("User with email " + email + " wasn't recognized as a student");
        }

        String base64Image = null;
        if (student.getImage() != null) {
            byte[] decompressedImage = decompressImage(student.getImage());
            base64Image = convertToBase64(decompressedImage);
        }

        model.addAttribute("studentImage", base64Image);

        String studentName = student.getFirstName() + " " + student.getLastName();
        studentName = capitalizeFirstLetter(studentName);

        String encodedName = URLEncoder.encode(studentName, StandardCharsets.UTF_8);
        Cookie studentNameCookie = new Cookie("studentName", encodedName);
        studentNameCookie.setPath("/");
        studentNameCookie.setMaxAge(24 * 60 * 60 * 30);
        response.addCookie(studentNameCookie);
    }

    public void setManageProfile(Model model, String email) {
        User existingUser = userRepository.findByEmail(email);
        if (existingUser == null) {
            throw new UserNotFoundException("User with email " + email + " wasn't found");
        }

        if (!(existingUser instanceof Student student)) {
            throw new UnauthorizedStudentAccessException("User with email " + email + " wasn't recognized as a student");
        }

        String base64Image = null;
        if (student.getImage() != null) {
            byte[] decompressedImage = decompressImage(student.getImage());
            base64Image = convertToBase64(decompressedImage);
        }

        String studentName = student.getFirstName() + " " + student.getLastName();
        studentName = capitalizeFirstLetter(studentName);

        model.addAttribute("email", student.getEmail());
        model.addAttribute("studentName", studentName);
        model.addAttribute("phoneNumber",student.getPhoneNumber());
        model.addAttribute("university", student.getUniversity());
        model.addAttribute("major", student.getMajor());
        model.addAttribute("address",student.getAddress());
        model.addAttribute("graduationYear",student.getGraduationYear());
        model.addAttribute("studentImage", base64Image);
    }

    @Transactional
    public ApiResponse updateStudentDetails(@RequestBody StudentInfoDto infoDto,
                                            String email) {
        try {

            StudentInfo studentInfo = new StudentInfo().toEntity(infoDto);

            User existingUser = userRepository.findByEmail(email);
            if (existingUser == null) {
                throw new UserNotFoundException("User with email " + email + " wasn't found");
            }

            if (!(existingUser instanceof Student student)) {
                throw new UnauthorizedStudentAccessException("User with email " + email + " wasn't recognized as a student");
            }

//        if (userData.containsKey("password") && userData.get("password") != null) {
//            String hashedPassword = PasswordHashingUtil.hashPassword((String) userData.get("password"));
//            company.setPassword(hashedPassword);
//        }

            if (studentInfo.getFirstName() != null) {
                student.setFirstName(studentInfo.getFirstName());
            }

            if (studentInfo.getLastName() != null) {
                student.setLastName(studentInfo.getLastName());
            }

            if (infoDto.getStudentImage() != null) {
                byte[] imageBytes = saveImage(infoDto.getStudentImage());
                student.setImage(imageBytes);
            }

            if (studentInfo.getAddress() != null) {
                student.setAddress(studentInfo.getAddress());
            }

            if (studentInfo.getGraduationYear() != null) {
                student.setGraduationYear(studentInfo.getGraduationYear());
            }

            if (studentInfo.getPhoneNumber() != null) {
                student.setPhoneNumber(studentInfo.getPhoneNumber());
            }

            if (studentInfo.getUniversity() != null) {
                student.setUniversity(studentInfo.getUniversity());
            }

            if (studentInfo.getMajor() != null) {
                student.setMajor(studentInfo.getMajor());
            }

            userRepository.save(student);
            return new ApiResponse("Student details updated successfully", HttpStatus.OK);
        } catch (UserNotFoundException | UnauthorizedStudentAccessException ex) {
            return new ApiResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
