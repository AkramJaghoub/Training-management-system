package ju.example.training_management_system.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/dashboard")
    public String setUpAdminDashboard(){
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
