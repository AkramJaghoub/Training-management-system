package ju.example.training_management_system.controller;


import ju.example.training_management_system.service.RegisterService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@AllArgsConstructor
public class RegisterController {

    RegisterService registerService;

    @GetMapping("/register")
    public String getRegistrationPage(){
        return "register-page";
    }

    @PostMapping("/register")
    public Map<String, String> register(@RequestBody Map<String, Object> userData) {
        registerService.registerUser(userData);
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        return response;
    }
}
