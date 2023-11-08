package ju.example.training_management_system.controller;

import jakarta.servlet.http.HttpSession;
import ju.example.training_management_system.model.users.Role;
import ju.example.training_management_system.service.AuthenticationService;
import ju.example.training_management_system.service.ResetPasswordService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


// TODO: Implement Spring security, JWT, two-factor authentication

@Controller
@AllArgsConstructor
public class LoginController {

    private final AuthenticationService authenticationService;
    private final ResetPasswordService resetPasswordService;

    @GetMapping("/login")
    public String getLoginPage(){
        return "login-page";
    }

    @PostMapping("/login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        HttpSession session) {

        if (authenticationService.isAdmin(email, password)) {
            Role role = Role.ADMIN;
            return "redirect:/" + role.toString().toLowerCase() + "/dashboard"; // return early for admin
        }

        Role role = authenticationService.getUserRole(email);

        if (!authenticationService.isValidUser(email, password)) {
            return "invalid credentials";
        }

        session.setAttribute("email", email);
        return "redirect:/" + role.toString().toLowerCase() + "/dashboard";
    }

    @GetMapping("/forget-password")
    public String getForgetPasswordPage(@RequestParam(value = "email", required = false) String email,
                                        Model model){
        if(email != null) {
            model.addAttribute("email", email);
        }
        return "forget-password";
    }

    @PostMapping("/forget-password")
    public String forgetPassword(@RequestParam("email") String email){
        resetPasswordService.sendEmail(email);
        return "forget-password";
    }

    @GetMapping("/reset-password")
    public String getResetPasswordPage(@RequestParam("token") String token,
                                       Model model) {
        if (!resetPasswordService.isTokenExpired(token)) {
            model.addAttribute("errorMessage", "Password reset link has expired.");
            return "error-page";
        }
        String email = resetPasswordService.getEmailFromToken(token);
        model.addAttribute("email", email);
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password/{token}")
    public String resetPassword(@PathVariable("token") String token,
                                @RequestParam("email") String email,
                                @RequestParam("newPassword") String newPassword) {
        resetPasswordService.resetPassword(email, newPassword, token);
        return "success-page";
    }
}
