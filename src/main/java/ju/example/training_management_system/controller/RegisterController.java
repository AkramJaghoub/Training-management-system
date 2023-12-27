package ju.example.training_management_system.controller;

import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.model.users.Role;
import ju.example.training_management_system.service.RegisterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;

import static ju.example.training_management_system.util.Utils.getRequiredDashboard;

@Controller
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService registerService;

    @GetMapping("/register")
    public String getRegistrationPage() {
        return "register-page";
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> userData) {
        ApiResponse response = registerService.registerUser(userData);

        if (response.getStatus() == HttpStatus.OK) {
            Role role = Role.toRole((String) userData.get("role"));
            Map<String, Object> body = new HashMap<>();
            body.put("redirectUrl", getRequiredDashboard(role));
            body.put("message", response.getMessage());
            return new ResponseEntity<>(body, response.getStatus());
        }
        return new ResponseEntity<>(response, response.getStatus());
    }
}