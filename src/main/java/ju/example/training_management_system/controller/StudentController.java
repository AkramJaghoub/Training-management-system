package ju.example.training_management_system.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ju.example.training_management_system.service.student.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
            studentService.setUpStudentDashboard(model, email, response);
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
}
