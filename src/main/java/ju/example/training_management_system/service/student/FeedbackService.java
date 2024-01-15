package ju.example.training_management_system.service.student;

import static ju.example.training_management_system.model.PostStatus.*;
import static org.springframework.http.HttpStatus.*;

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

      Student student =
          studentRepository
              .findById(studentId)
              .orElseThrow(
                  () ->
                      new UserNotFoundException(
                          "Student with id [" + studentId + "] was not found"));

      Company company = companyRepository.findByCompanyName(feedbackDto.getCompanyName());

      Feedback feedback = new Feedback().toEntity(feedbackDto);
      feedback.setStudent(student);
      feedback.setCompany(company);

      feedbackRepository.save(feedback);

      // Update company rating
      company.addRating(feedbackDto.getRating());
      companyRepository.save(company);

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

      Student student =
          studentRepository
              .findById(studentId)
              .orElseThrow(
                  () ->
                      new UserNotFoundException(
                          "Student with id [" + studentId + "] was not found"));

      Feedback existingFeedback =
          feedbackRepository
              .findById(feedbackId)
              .orElseThrow(
                  () ->
                      new FeedbackDoesNotExistException(
                          "Feedback with id [" + feedbackId + " was not found"));

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
      User user =
          userRepository
              .findById(userId)
              .orElseThrow(
                  () -> new UserNotFoundException("User with id [" + userId + "] was not found"));

      if (!(user instanceof Student)) {
        throw new UnauthorizedStudentAccessException("User is not a student");
      }

      if (!feedbackRepository.existsById(feedbackId)) {
        throw new FeedbackDoesNotExistException(
            "Feedback with id [" + feedbackId + "] does not exist");
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
    return feedbackRepository.findTopByOrderByPostDateDesc().orElse(null);
  }
}
