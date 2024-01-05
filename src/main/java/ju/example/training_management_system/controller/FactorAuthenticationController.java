package ju.example.training_management_system.controller;

import ju.example.training_management_system.model.TwoFactorAuthentication;
import ju.example.training_management_system.service.login.FactorAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/auth/2fa")
public class FactorAuthenticationController {

    private final FactorAuthenticationService factorAuthenticationService;

    @GetMapping("/confirm-email")
    public String getEmailConfirmationPageFor2FA(@RequestParam(value = "email", required = false) String email,
                                                 Model model) {
        if (email != null) {
            model.addAttribute("email", email);
        }
        return "2FA-email-confirmation";
    }

    @PostMapping("/confirm-email")
    public String sendConfirmationEmailFor2FA(@RequestParam("email") String email) {
        factorAuthenticationService.sendEmail(email);
        return "redirect:/auth/2fa/code-entry?email=" + email;
    }

    @GetMapping("/code-entry")
    public String getAuthenticationCodePage(@RequestParam("email") String email, Model model) {
        if (!factorAuthenticationService.isTokenExpired(email)) {
            model.addAttribute("errorMessage", "Authentication token has expired.");
            return "redirect:/auth/2fa/confirm-email?email=" + email + "&tokenExpired=true";
        }
        TwoFactorAuthentication token = factorAuthenticationService.getTokenByEmail(email);
        model.addAttribute("token", token.getToken());
        model.addAttribute("email", email);
        return "2FA";
    }

    @PostMapping("/code-entry")
    public String authenticateCode(@RequestParam("email") String email) {
        if (email != null) {
            factorAuthenticationService.process2FA(email);
        }
        return "2FA";
    }
}
