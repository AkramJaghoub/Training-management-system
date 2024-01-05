package ju.example.training_management_system.controller;

import ju.example.training_management_system.service.login.ResetPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ForgetPasswordController {

    private final ResetPasswordService resetPasswordService;

    @GetMapping("/forget-password")
    public String getForgetPasswordPage(@RequestParam(value = "email", required = false) String email,
                                        Model model) {
        if (email != null) {
            model.addAttribute("email", email);
        }
        return "forget-password";
    }

    @PostMapping("/forget-password")
    public String forgetPassword(@RequestParam("email") String email) {
        resetPasswordService.sendEmail(email);
        return "forget-password";
    }

    @GetMapping("/reset-password")
    public String getResetPasswordPage(@RequestParam("token") String token,
                                       Model model) {
        if (!resetPasswordService.isTokenExpired(token)) {
            model.addAttribute("errorMessage", "Password reset link has expired.");
            return "redirect:/forget-password";
        }
        String email = resetPasswordService.getEmailFromToken(token);
        model.addAttribute("email", email);
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam("email") String email,
                                @RequestParam("newPassword") String newPassword) {
        if (email != null) {
            resetPasswordService.resetPassword(email, newPassword);
        }
        return "reset-password";
    }
}
