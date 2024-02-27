package ju.example.training_management_system.service.student;

import static ju.example.training_management_system.model.PostStatus.*;
import static org.springframework.http.HttpStatus.*;

import ju.example.training_management_system.entity.FeedbackEntity;
import ju.example.training_management_system.entity.users.CompanyEntity;
import ju.example.training_management_system.entity.users.StudentEntity;
import ju.example.training_management_system.entity.users.UserEntity;
import ju.example.training_management_system.exception.FeedbackDoesNotExistException;
import ju.example.training_management_system.exception.UnauthorizedCompanyAccessException;
import ju.example.training_management_system.exception.UnauthorizedStudentAccessException;
import ju.example.training_management_system.exception.UserNotFoundException;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.model.Feedback;
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

  public ApiResponse provideFeedback(Feedback feedback) {
    try {
      long studentId = feedback.getStudentId();

      StudentEntity student =
          studentRepository
              .findById(studentId)
              .orElseThrow(
                  () ->
                      new UserNotFoundException(
                          "StudentEntity with id [" + studentId + "] was not found"));

      CompanyEntity company = companyRepository.findByCompanyName(feedback.getCompanyName());

      FeedbackEntity feedbackEntity = new FeedbackEntity().toEntity(feedback);
      feedbackEntity.setStudent(student);
      feedbackEntity.setCompany(company);

      feedbackRepository.save(feedbackEntity);

      // Update company rating
      company.addRating(feedbackEntity.getRating());
      companyRepository.save(company);

      return new ApiResponse("StudentEntity feedback was created successfully", CREATED);
    } catch (UserNotFoundException ex) {
      return new ApiResponse(ex.getMessage(), BAD_REQUEST);
    } catch (UnauthorizedCompanyAccessException ex) {
      return new ApiResponse(ex.getMessage(), UNAUTHORIZED);
    }
  }

  public ApiResponse updateFeedback(Feedback feedback, long feedbackId) {
    try {
      long studentId = feedback.getStudentId();

      StudentEntity student =
          studentRepository
              .findById(studentId)
              .orElseThrow(
                  () ->
                      new UserNotFoundException(
                          "StudentEntity with id [" + studentId + "] was not found"));

      FeedbackEntity existingFeedback =
          feedbackRepository
              .findById(feedbackId)
              .orElseThrow(
                  () ->
                      new FeedbackDoesNotExistException(
                          "Feedback with id [" + feedbackId + " was not found"));

      CompanyEntity company = companyRepository.findByCompanyName(feedback.getCompanyName());

      existingFeedback.setComment(feedback.getComment());
      existingFeedback.setRating(feedback.getRating());
      existingFeedback.setStatus(PENDING);
      existingFeedback.setStudent(student);
      existingFeedback.setCompany(company);

      feedbackRepository.save(existingFeedback);
      return new ApiResponse("StudentEntity feedback was updated successfully", OK);
    } catch (UserNotFoundException | FeedbackDoesNotExistException ex) {
      return new ApiResponse(ex.getMessage(), BAD_REQUEST);
    } catch (UnauthorizedCompanyAccessException ex) {
      return new ApiResponse(ex.getMessage(), UNAUTHORIZED);
    }
  }

  public ApiResponse deleteFeedback(long feedbackId, long userId) {
    try {
      UserEntity user =
          userRepository
              .findById(userId)
              .orElseThrow(
                  () ->
                      new UserNotFoundException(
                          "UserEntity with id [" + userId + "] was not found"));

      if (!(user instanceof StudentEntity)) {
        throw new UnauthorizedStudentAccessException("UserEntity is not a student");
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

  public FeedbackEntity getLatestFeedback() {
    return feedbackRepository.findTopByOrderByPostDateDesc().orElse(null);
  }
}
