package ju.example.training_management_system.controller;

import ju.example.training_management_system.service.AdminService.AdminService;
import ju.example.training_management_system.service.company.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public String setUpAdminDashboard(Model model){
        adminService.setUpAdminDashboard(model);
        return "/admin/admin-dashboard";
    }

    @GetMapping("/users")
    public String setUpAdminUserList(){
        return "/admin/users";
    }

    @GetMapping("/advertisements")
    public String setUpAdminAdvertisementList(){
        return "/admin/advertisement-data";
    }
}
