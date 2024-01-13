package ju.example.training_management_system.controller;

import ju.example.training_management_system.dto.FeedbackDto;
import ju.example.training_management_system.model.ApiResponse;
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
    public ResponseEntity<?> provideFeedback(@RequestBody FeedbackDto feedbackDto) {
        ApiResponse apiResponse = feedbackService.provideFeedback(feedbackDto);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateFeedback(@RequestBody FeedbackDto feedbackDto,
                                            @RequestParam("feedbackId") long feedbackId){
        ApiResponse apiResponse = feedbackService.updateFeedback(feedbackDto, feedbackId);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFeedback(@RequestParam("feedbackId") long feedbackId,
                                            @RequestParam("studentId") long studentId) {
        ApiResponse apiResponse = feedbackService.deleteFeedback(feedbackId, studentId);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }
}
