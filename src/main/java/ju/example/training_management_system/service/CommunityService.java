package ju.example.training_management_system.service;

import ju.example.training_management_system.dto.FeedbackDto;
import ju.example.training_management_system.exception.FeedbackDoesNotExistException;
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

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static ju.example.training_management_system.model.PostStatus.APPROVED;
import static ju.example.training_management_system.util.Utils.convertToBase64;
import static ju.example.training_management_system.util.Utils.decompressImage;
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

            if (!(user instanceof Student student)) {
                throw new UnauthorizedStudentAccessException("User with email " + email + " wasn't recognized as a student");
            }

            List<Feedback> feedbackList = getFeedbacksByPostDateAndStatus();
            List<Feedback> studentFeedback = getFeedbacksByStudentIdAndPostDate(user.getId());

            String base64Image = null;
            if (nonNull(student.getImage())) {
                byte[] decompressedImage = decompressImage(student.getImage());
                base64Image = convertToBase64(decompressedImage);
            }

            String studentName = student.getFirstName() + " " + student.getLastName();

            model.addAttribute("studentName", studentName);
            model.addAttribute("studentImage", base64Image);
            model.addAttribute("feedbackList", feedbackList);
            model.addAttribute("studentFeedback", studentFeedback);

            return new ApiResponse("Set up was correctly done", OK);
        } catch (UserNotFoundException ex) {
            return new ApiResponse(ex.getMessage(), BAD_REQUEST);
        } catch (UnauthorizedStudentAccessException ex) {
            return new ApiResponse(ex.getMessage(), UNAUTHORIZED);
        }
    }

    private List<Feedback> getFeedbacksByStudentIdAndPostDate(Long studentId) {
        return feedbackRepository.findByStudent_Id(studentId)
                .stream()
                .sorted(Comparator.comparing(Feedback::getPostDate).reversed())
                .toList();
    }

    private List<Feedback> getFeedbacksByPostDateAndStatus() {
        return feedbackRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Feedback::getPostDate).reversed())
                .filter((feedback) -> feedback.getStatus().equals(APPROVED))
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
            return new ApiResponse("Student feedback was created successfully", CREATED);
        } catch (UserNotFoundException ex) {
            return new ApiResponse(ex.getMessage(), BAD_REQUEST);
        } catch (UnauthorizedCompanyAccessException ex){
            return new ApiResponse(ex.getMessage(), UNAUTHORIZED);
        }
    }

    public ApiResponse deleteFeedback(long feedbackId, long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User with id [" + userId + "] was not found"));

            if(!(user instanceof Student)){
                throw new UnauthorizedStudentAccessException("User is not a student");
            }

            if (!feedbackRepository.existsById(feedbackId)) {
                throw new FeedbackDoesNotExistException("Feedback with id [" + feedbackId + "] does not exist");
            }

            feedbackRepository.deleteById(feedbackId);
            return new ApiResponse("Feedback was deleted successfully", OK);
        } catch (FeedbackDoesNotExistException | UserNotFoundException ex) {
            return new ApiResponse(ex.getMessage(), BAD_REQUEST);
        } catch (UnauthorizedCompanyAccessException ex){
            return new ApiResponse(ex.getMessage(), UNAUTHORIZED);
        }
    }
}