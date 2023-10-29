package ju.example.training_management_system.controller;

import ju.example.training_management_system.model.Role;
import ju.example.training_management_system.model.User;
import ju.example.training_management_system.service.AuthenticationService;
import ju.example.training_management_system.service.ResetPasswordService;
import ju.example.training_management_system.util.Utils;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@AllArgsConstructor
public class LoginController {

    private final AuthenticationService authenticationService;
    private final ResetPasswordService resetPasswordService;
    private static final Logger logger = (Logger) LoggerFactory.getLogger(LoginController.class);


    @GetMapping("/login")
    public String getLoginPage(){
        return "login-page";
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<?> login(@RequestBody User user, Model model) {

        if (!authenticationService.isAdmin(user)) {
            Role role = Role.ADMIN;
            return ResponseEntity.ok(Utils.getRequiredDashboard(role)); // return early for admin
        }

        Role role = authenticationService.getUserRole(user.getEmail());

        if (!authenticationService.isValidUser(user)) {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }

        model.addAttribute("email", user.getEmail());
        // TODO: Implement Spring security, JWT, two-factor authentication
        return ResponseEntity.ok(Utils.getRequiredDashboard(role));
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
        System.out.println("gggggggggggggggggggg");
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
