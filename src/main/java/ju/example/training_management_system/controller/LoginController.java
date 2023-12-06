package ju.example.training_management_system.controller;

import jakarta.servlet.http.HttpSession;
import ju.example.training_management_system.dto.LoginDto;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.service.LoginService;
import ju.example.training_management_system.service.ResetPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

// TODO: Implement Spring security, JWT, two-factor authentication

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    private final ResetPasswordService resetPasswordService;

    @GetMapping("/login")
    public String getLoginPage(){
        return "login-page";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto,
                                   HttpSession session) {
        ApiResponse response = loginService.loginUser(loginDto, session);
        return new ResponseEntity<>(response, response.getStatus());
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
        System.out.println(email);
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
