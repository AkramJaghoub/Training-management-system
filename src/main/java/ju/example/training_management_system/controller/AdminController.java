package ju.example.training_management_system.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.service.AdminService.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final HttpServletRequest request;

    @GetMapping("/dashboard")
    public String setUpAdminDashboard(Model model){
        HttpSession session = request.getSession();

        String email = (String) session.getAttribute("email");
        if(email != null){
            adminService.setUpAdminDashboardPage(model);
            return "/admin/admin-dashboard";
        }
        return "redirect:/login";
    }

    @GetMapping("/users")
    public String setUpAdminUserList(Model model){
        HttpSession session = request.getSession();

        String email = (String) session.getAttribute("email");
        if(email != null){
            adminService.setUpUserListPage(model);
            return "/admin/users";
        }
        return "redirect:/login";
    }

    @GetMapping("/advertisements")
    public String setUpAdminAdvertisementList(Model model){
        HttpSession session = request.getSession();

        String email = (String) session.getAttribute("email");
        if(email != null){
            adminService.setUpAdsListPage(model);
            return "/admin/advertisement-data";
        }
        return "redirect:/login";
    }

    @DeleteMapping("/delete/user/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") long userId){
        ApiResponse response = adminService.deleteUser(userId);
        return ResponseEntity.status(response.getStatus()).body(response.getMessage());
    }
}