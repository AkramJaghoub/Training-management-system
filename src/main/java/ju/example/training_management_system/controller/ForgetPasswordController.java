package ju.example.training_management_system.controller;

import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.service.login.ResetPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static ju.example.training_management_system.util.Utils.redirectToPage;

@Controller
@RequiredArgsConstructor
public class ForgetPasswordController {

    private final ResetPasswordService resetPasswordService;

    @GetMapping("/forget-password")
    public String getForgetPasswordPage(@RequestParam(value = "email", required = false) String email,
                                        Model model) {
        if (nonNull(email)) {
            model.addAttribute("email", email);
        }
        return "forget-password";
    }


    @PostMapping("/forget-password")
    public ResponseEntity<?> forgetPassword(@RequestParam("email") String email) {
        ApiResponse apiResponse = resetPasswordService.sendEmail(email);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    @GetMapping("/reset-password")
    public String getResetPasswordPage(@RequestParam("token") String token,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {
        if (resetPasswordService.isTokenExpired(token)) {
            redirectAttributes.addAttribute("errorMessage", "expired");
            return "redirect:/forget-password";
        }
        String email = resetPasswordService.getEmailFromToken(token);
        model.addAttribute("email", email);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam("email") String email,
                                @RequestParam("newPassword") String newPassword) {
        if (isNull(email)) {
            return redirectToPage("/forget-password");
        }
        ApiResponse apiResponse = resetPasswordService.resetPassword(email, newPassword);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }
}
