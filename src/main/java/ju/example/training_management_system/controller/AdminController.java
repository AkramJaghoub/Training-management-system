package ju.example.training_management_system.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.service.AdminService.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static java.util.Objects.isNull;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final HttpServletRequest request;

    @GetMapping("/dashboard")
    public String setUpAdminDashboard(Model model) {
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");
        if (isNull(email)) {
            return "redirect:/login";
        }

        adminService.setUpAdminDashboardPage(model);
        return "/admin/admin-dashboard";
    }

    @GetMapping("/users")
    public String setUpAdminUserList(Model model) {
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");
        if (isNull(email)) {
            return "redirect:/login";
        }

        adminService.setUpUserListPage(model);
        return "/admin/users";
    }

    @GetMapping("/advertisements")
    public String setUpAdminAdvertisementList(Model model) {
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");
        if (isNull(email)) {
            return "redirect:/login";
        }

        adminService.setUpAdsListPage(model);
        return "/admin/advertisement-data";
    }

    @GetMapping("/community")
    public String setUpCommunityPage(Model model){
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("email");
        if (isNull(email)) {
            return "redirect:/login";
        }

        adminService.setUpStudentsFeedbackPage(model);
        return "/admin/community";
    }

    @DeleteMapping("/delete/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") long userId) {
        ApiResponse response = adminService.deleteUser(userId);
        return ResponseEntity.status(response.getStatus()).body(response.getMessage());
    }

    @PutMapping("/update/ad-status/{id}")
    public ResponseEntity<?> updateAdStatus(@PathVariable("id") long adId, @RequestHeader("newStatus") String newStatus) {
        ApiResponse response = adminService.updateAdStatus(adId, newStatus);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PutMapping("/update/feedback-status/{feedbackId}")
    public ResponseEntity<?> updateFeedbackStatus(@PathVariable("feedbackId") long feedbackId,
                                                  @RequestHeader("newStatus") String newStatus) {
        ApiResponse response = adminService.updateFeedbackStatus(feedbackId, newStatus);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}