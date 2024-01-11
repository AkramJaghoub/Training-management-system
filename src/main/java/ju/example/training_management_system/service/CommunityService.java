package ju.example.training_management_system.service;

import ju.example.training_management_system.dto.FeedbackDto;
import ju.example.training_management_system.exception.UnauthorizedCompanyAccessException;
import ju.example.training_management_system.exception.UnauthorizedStudentAccessException;
import ju.example.training_management_system.exception.UserNotFoundException;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.model.Feedback;
import ju.example.training_management_system.model.users.Company;
import ju.example.training_management_system.model.users.Student;
import ju.example.training_management_system.model.users.User;
import ju.example.training_management_system.repository.FeedbackRepository;
import ju.example.training_management_system.repository.users.CompanyRepository;
import ju.example.training_management_system.repository.users.StudentRepository;
import ju.example.training_management_system.repository.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Comparator;
import java.util.List;

import static java.time.LocalDateTime.now;
import static java.util.Objects.isNull;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final StudentRepository studentRepository;
    private final FeedbackRepository feedbackRepository;

    public ApiResponse setUpCommunityPage(Model model, String email) {
        try {
            User user = userRepository.findByEmail(email);

            if (isNull(user)) {
                throw new UserNotFoundException("User with email " + email + " wasn't found");
            }

            if (!(user instanceof Student)) {
                throw new UnauthorizedStudentAccessException("User with email " + email + " wasn't recognized as a student");
            }

            List<Feedback> feedbackList = getFeedbacksByPostDate();
            model.addAttribute("feedbackList", feedbackList);

            return new ApiResponse("Set up was correctly done", OK);
        } catch (UserNotFoundException ex) {
            return new ApiResponse(ex.getMessage(), BAD_REQUEST);
        } catch (UnauthorizedStudentAccessException ex) {
            return new ApiResponse(ex.getMessage(), UNAUTHORIZED);
        }
    }

    private List<Feedback> getFeedbacksByPostDate(){
        return feedbackRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Feedback::getPostDate).reversed())
                .toList();
    }

    public ApiResponse provideFeedback(FeedbackDto feedbackDto) {
        try {
            long studentId = feedbackDto.getStudentId();

            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new UserNotFoundException("Student with id [" + studentId + "] was not found"));

            Company company = companyRepository.findByCompanyName(feedbackDto.getCompanyName());

            Feedback feedback = new Feedback().toEntity(feedbackDto);
            feedback.setStudent(student);
            feedback.setCompany(company);

            feedbackRepository.save(feedback);
            return new ApiResponse("Student feedback was set successfully", CREATED);
        } catch (UserNotFoundException | UnauthorizedCompanyAccessException ex) {
            return new ApiResponse(ex.getMessage(), BAD_REQUEST);
        }
    }
}