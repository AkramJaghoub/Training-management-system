package ju.example.training_management_system.controller;

import static java.util.Objects.isNull;
import static ju.example.training_management_system.util.Utils.redirectToPage;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import ju.example.training_management_system.dto.CompanyInfoDto;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.service.company.CompanyService;
import lombok.RequiredArgsConstructor;
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
  private final HttpServletRequest request;

  @GetMapping("/dashboard")
  public String getCompanyDashboard(Model model) {
    HttpSession session = request.getSession();
    String email = (String) session.getAttribute("email");

    if (isNull(email)) {
      return "redirect:/login";
    }

    ApiResponse response = companyService.setUpCompanyDashboard(model, email);
    return response.getStatus() == HttpStatus.OK ? "/company/company-dashboard" : "redirect:/login";
  }

  @GetMapping("/manage-profile")
  public String getManageProfilePage(Model model) {
    HttpSession session = request.getSession();
    String email = (String) session.getAttribute("email");
    if (isNull(email)) {
      return "redirect:/login";
    }

    ApiResponse response = companyService.setManageProfile(model, email);
    return response.getStatus() == HttpStatus.OK ? "/company/manage-profile" : "redirect:/login";
  }

  @PutMapping("/manage-profile")
  public ResponseEntity<?> manageProfile(@ModelAttribute CompanyInfoDto infoDto) {
    HttpSession session = request.getSession();
    String email = (String) session.getAttribute("email");
    if (isNull(email)) {
      return redirectToPage("/login");
    }

    ApiResponse response = companyService.updateCompanyDetails(infoDto, email);
    return ResponseEntity.status(response.getStatus()).body(response);
  }
}
