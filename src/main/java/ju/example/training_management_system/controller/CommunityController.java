package ju.example.training_management_system.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import ju.example.training_management_system.dto.FeedbackDto;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static java.util.Objects.isNull;

@Controller
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;
    private final HttpServletRequest request;

    @GetMapping("")
    public String getCommunityPage(Model model) {
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");

        if (isNull(email)) {
            return "redirect:/login";
        }

        ApiResponse response = communityService.setUpCommunityPage(model, email);
        return response.getStatus() == HttpStatus.OK ? "/student/community"
                : "redirect:/login";
    }

    @PostMapping("/provide-feedback")
    public ResponseEntity<?> provideFeedback(@RequestBody FeedbackDto feedbackDto) {
        ApiResponse apiResponse = communityService.provideFeedback(feedbackDto);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    @DeleteMapping("/delete-feedback")
    public ResponseEntity<?> deleteFeedback(long feedbackId, long studentId) {
        ApiResponse apiResponse = communityService.deleteFeedback(feedbackId, studentId);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }
}