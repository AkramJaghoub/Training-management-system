package ju.example.training_management_system.controller;

import jakarta.servlet.http.HttpSession;
import ju.example.training_management_system.model.users.Role;
import ju.example.training_management_system.service.CompanyService;
import ju.example.training_management_system.util.Utils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@AllArgsConstructor
@RequestMapping("/company")
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping("/dashboard")
    public String setUpCompanyDashboard(HttpSession session,
                                        Model model) {

        String email = (String) session.getAttribute("email");
        if(email != null){
            String companyName = companyService.getCompanyName(email);
            model.addAttribute("companyName", companyName);
        }

        session.setAttribute("email", email);

        return Utils.getRequiredDashboard(Role.COMPANY);
    }

    @GetMapping("/manage-profile")
    public String getManageProfilePage(Model model,
                                HttpSession httpSession) {

        String email = (String) httpSession.getAttribute("email");
        if (email != null) {
            String companyName = companyService.getCompanyName(email);
            model.addAttribute("companyName", companyName);
        }

        return "manage-profile";
    }


    @PostMapping("/manage-profile")
    public String manageProfile(@RequestBody Map<String, Object> userData){
        System.out.println(userData.entrySet());
        companyService.updateCompanyDetails(userData);
        return null;
    }

}