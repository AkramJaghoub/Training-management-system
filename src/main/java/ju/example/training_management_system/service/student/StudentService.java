package ju.example.training_management_system.service.student;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import ju.example.training_management_system.exception.UnauthorizedStudentAccessException;
import ju.example.training_management_system.exception.UserNotFoundException;
import ju.example.training_management_system.model.users.Student;
import ju.example.training_management_system.model.users.User;
import ju.example.training_management_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static ju.example.training_management_system.util.Utils.convertToBase64;
import static ju.example.training_management_system.util.Utils.decompressImage;

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

        String encodedName = URLEncoder.encode(studentName, StandardCharsets.UTF_8);
        Cookie studentNameCookie = new Cookie("studentName", encodedName);
        studentNameCookie.setPath("/");
        studentNameCookie.setMaxAge(24 * 60 * 60 * 30);
        response.addCookie(studentNameCookie);
    }
}
