package ju.example.training_management_system.controller;

import ju.example.training_management_system.entity.FeedbackEntity;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.model.Feedback;
import ju.example.training_management_system.service.student.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/feedback")
@RequiredArgsConstructor
public class FeedbackController {

  private final FeedbackService feedbackService;

  @PostMapping("/provide")
  public ResponseEntity<?> provideFeedback(@RequestBody Feedback feedback) {
    ApiResponse apiResponse = feedbackService.provideFeedback(feedback);
    return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
  }

  @GetMapping("/latest")
  public ResponseEntity<FeedbackEntity> getLatestFeedback() {
    FeedbackEntity latestFeedback = feedbackService.getLatestFeedback();
    if (latestFeedback != null) {
      return ResponseEntity.ok(latestFeedback);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @PutMapping("/update")
  public ResponseEntity<?> updateFeedback(
      @RequestBody Feedback feedback, @RequestParam("feedbackId") long feedbackId) {
    ApiResponse apiResponse = feedbackService.updateFeedback(feedback, feedbackId);
    return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
  }

  @DeleteMapping("/delete")
  public ResponseEntity<?> deleteFeedback(
      @RequestParam("feedbackId") long feedbackId, @RequestParam("studentId") long studentId) {
    ApiResponse apiResponse = feedbackService.deleteFeedback(feedbackId, studentId);
    return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
  }
}
