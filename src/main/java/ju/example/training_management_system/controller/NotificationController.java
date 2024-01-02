package ju.example.training_management_system.controller;

import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.service.company.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {
    private final NotificationService notificationService;

    @DeleteMapping("/mark-as-read/{id}")
    public ResponseEntity<?> markNotificationAsRead(@PathVariable("id") long notificationId) {
        ApiResponse response = notificationService.markNotificationAsRead(notificationId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
