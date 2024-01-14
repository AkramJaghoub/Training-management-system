package ju.example.training_management_system.controller;

import static java.util.Objects.isNull;
import static ju.example.training_management_system.util.Utils.redirectToPage;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import ju.example.training_management_system.dto.StudentInfoDto;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.service.student.StudentService;
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
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {
  private final StudentService studentService;
  private final HttpServletRequest request;

  @GetMapping("/dashboard")
  public String setUpStudentDashboard(Model model) {
    HttpSession session = request.getSession();
    String email = (String) session.getAttribute("email");
    if (isNull(email)) {
      return "redirect:/login";
    }
    session.setAttribute("email", email);

    ApiResponse response = studentService.setUpStudentDashboard(model, email);
    return response.getStatus() == HttpStatus.OK ? "/student/student-dashboard" : "redirect:/login";
  }

  @GetMapping("/manage-profile")
  public String getStudentProfilePage(Model model) {
    HttpSession session = request.getSession();
    String email = (String) session.getAttribute("email");
    if (isNull(email)) {
      return "redirect:/login";
    }

    ApiResponse response = studentService.setManageProfile(model, email);
    return response.getStatus() == HttpStatus.OK ? "/student/manage-profile" : "redirect:/login";
  }

  @PutMapping("/manage-profile")
  public ResponseEntity<?> manageProfile(@ModelAttribute StudentInfoDto infoDto) {
    HttpSession session = request.getSession();
    String email = (String) session.getAttribute("email");
    if (isNull(email)) {
      return redirectToPage("/login");
    }

    ApiResponse response = studentService.updateStudentDetails(infoDto, email);
    return ResponseEntity.status(response.getStatus()).body(response);
  }
}
