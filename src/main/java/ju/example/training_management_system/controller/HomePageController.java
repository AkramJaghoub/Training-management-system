package ju.example.training_management_system.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tms")
public class HomePageController {

    @GetMapping("/home")
    public String getHomePage() {
        return "home-page";
    }
}
