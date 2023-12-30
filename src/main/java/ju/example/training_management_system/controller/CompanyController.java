package ju.example.training_management_system.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ju.example.training_management_system.dto.CompanyInfoDto;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.service.company.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;
    private final HttpServletResponse response;
    private final HttpServletRequest request;

    @GetMapping("/dashboard")
    public String setUpCompanyDashboard(Model model) {
        HttpSession session = request.getSession();

        String email = (String) session.getAttribute("email");
        if (email != null && !email.equals("root")) {
            companyService.setUpCompanyDashboard(model, email, response);
            session.setAttribute("email", email);
            return "/company/company-dashboard";
        }
        return "redirect:/login";
    }

    @GetMapping("/manage-profile")
    public String getManageProfilePage(Model model) {
        HttpSession session = request.getSession();

        String email = (String) session.getAttribute("email");
        if (email != null && !email.equals("root")) {
            companyService.setManageProfile(model, email);
            return "/company/manage-profile";
        }
        return "redirect:/login";
    }

    @PutMapping("/manage-profile")
    public ResponseEntity<?> manageProfile(@ModelAttribute CompanyInfoDto infoDto) {
        HttpSession session = request.getSession();

        String email = (String) session.getAttribute("email");
        if (email != null && !email.equals("root")) {
            ApiResponse apiResponse = companyService.updateCompanyDetails(infoDto, email);
            return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/login");
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}