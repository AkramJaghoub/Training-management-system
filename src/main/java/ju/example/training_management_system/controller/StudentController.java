package ju.example.training_management_system.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ju.example.training_management_system.dto.CompanyInfoDto;
import ju.example.training_management_system.dto.StudentInfoDto;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.service.student.StudentService;
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
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;
    private final HttpServletResponse response;
    private final HttpServletRequest request;

    @GetMapping("/dashboard")
    public String setUpStudentDashboard(Model model) {
        HttpSession session = request.getSession();

        String email = (String) session.getAttribute("email");
        if (email != null) {
            studentService.setUpStudentDashboard(model,email,response);
            session.setAttribute("email", email);
            return "/student/student-dashboard";
        }
        return "redirect:/login";
    }

    @GetMapping("/manage-profile")
    public String getStudentProfilePage(Model model){
        HttpSession session = request.getSession();

        String email = (String) session.getAttribute("email");
        if (email != null) {
            studentService.setManageProfile(model, email);
            return "/student/manage-profile";
        }
        return "redirect:/login";
    }

    @PutMapping("/manage-profile")
    public ResponseEntity<?> manageProfile(@ModelAttribute StudentInfoDto infoDto) {
        HttpSession session = request.getSession();

        String email = (String) session.getAttribute("email");
        if (email != null) {
            ApiResponse apiResponse = studentService.updateStudentDetails(infoDto, email);
            return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse.getMessage());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/login");
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
