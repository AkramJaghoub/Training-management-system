package ju.example.training_management_system.controller;

import jakarta.servlet.http.HttpSession;
import ju.example.training_management_system.dto.LoginDto;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

// TODO: Implement Spring security, JWT, two-factor authentication

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @GetMapping("/login")
    public String getLoginPage() {
        return "login-page";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto, HttpSession session) {
        ApiResponse response = loginService.loginUser(loginDto, session);
//        if (response.getStatus() == TEMPORARY_REDIRECT) {
//            HttpHeaders headers = new HttpHeaders();
//            headers.add("Location", "/auth/2fa/confirm-email");
//            return new ResponseEntity<>(headers, HttpStatus.FOUND);
//        }
        return new ResponseEntity<>(response, response.getStatus());
    }
}
