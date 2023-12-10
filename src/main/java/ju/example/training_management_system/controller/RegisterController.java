package ju.example.training_management_system.controller;

import ju.example.training_management_system.model.users.Role;
import ju.example.training_management_system.service.RegisterService;
import ju.example.training_management_system.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService registerService;

    @GetMapping("/register")
    public String getRegistrationPage() {
        return "register-page";
    }

    @PostMapping("/register")
    public String register(@RequestBody Map<String, Object> userData) {
        registerService.registerUser(userData);
        Role role = Role.toRole((String) userData.get("role"));
        return Utils.getRequiredDashboard(role);
    }
}
