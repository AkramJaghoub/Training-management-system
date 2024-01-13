package ju.example.training_management_system.service.student;

import ju.example.training_management_system.dto.FeedbackDto;
import ju.example.training_management_system.exception.FeedbackDoesNotExistException;
import ju.example.training_management_system.exception.UnauthorizedCompanyAccessException;
import ju.example.training_management_system.exception.UnauthorizedStudentAccessException;
import ju.example.training_management_system.exception.UserNotFoundException;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.model.Feedback;
import ju.example.training_management_system.model.PostStatus;
import ju.example.training_management_system.model.users.Company;
import ju.example.training_management_system.model.users.Student;
import ju.example.training_management_system.model.users.User;
import ju.example.training_management_system.repository.FeedbackRepository;
import ju.example.training_management_system.repository.users.CompanyRepository;
import ju.example.training_management_system.repository.users.StudentRepository;
import ju.example.training_management_system.repository.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static java.util.Objects.nonNull;
import static ju.example.training_management_system.model.PostStatus.*;
import static ju.example.training_management_system.util.Utils.convertToBase64;
import static ju.example.training_management_system.util.Utils.decompressImage;
import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final StudentRepository studentRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

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
        } catch (UnauthorizedCompanyAccessException ex) {
            return new ApiResponse(ex.getMessage(), UNAUTHORIZED);
        }
    }

    public ApiResponse updateFeedback(FeedbackDto feedbackDto, long feedbackId) {
        try {
            long studentId = feedbackDto.getStudentId();

            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new UserNotFoundException("Student with id [" + studentId + "] was not found"));

            Feedback existingFeedback = feedbackRepository.findById(feedbackId)
                    .orElseThrow(() -> new FeedbackDoesNotExistException("Feedback with id [" + feedbackId + " was not found"));

            Company company = companyRepository.findByCompanyName(feedbackDto.getCompanyName());

            existingFeedback.setComment(feedbackDto.getComment());
            existingFeedback.setRating(feedbackDto.getRating());
            existingFeedback.setStatus(PENDING);
            existingFeedback.setStudent(student);
            existingFeedback.setCompany(company);

            feedbackRepository.save(existingFeedback);
            return new ApiResponse("Student feedback was updated successfully", OK);
        } catch (UserNotFoundException | FeedbackDoesNotExistException ex) {
            return new ApiResponse(ex.getMessage(), BAD_REQUEST);
        } catch (UnauthorizedCompanyAccessException ex) {
            return new ApiResponse(ex.getMessage(), UNAUTHORIZED);
        }
    }

    public ApiResponse deleteFeedback(long feedbackId, long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User with id [" + userId + "] was not found"));

            if (!(user instanceof Student)) {
                throw new UnauthorizedStudentAccessException("User is not a student");
            }

            if (!feedbackRepository.existsById(feedbackId)) {
                throw new FeedbackDoesNotExistException("Feedback with id [" + feedbackId + "] does not exist");
            }

            feedbackRepository.deleteById(feedbackId);
            return new ApiResponse("Feedback was deleted successfully", OK);
        } catch (FeedbackDoesNotExistException | UserNotFoundException ex) {
            return new ApiResponse(ex.getMessage(), BAD_REQUEST);
        } catch (UnauthorizedCompanyAccessException ex) {
            return new ApiResponse(ex.getMessage(), UNAUTHORIZED);
        }
    }

    public Feedback getLatestFeedback() {
        Feedback latestFeedback = feedbackRepository.findTopByOrderByPostDateDesc().orElse(null);
        if (nonNull(latestFeedback)) {
            byte[] compressedImage = latestFeedback.getStudent().getImage();
            byte[] decompressedImage = decompressImage(compressedImage);
            String base64Image = convertToBase64(decompressedImage);
            latestFeedback.setDecompressedImageBase64(base64Image);
        }
        return latestFeedback;
    }
}