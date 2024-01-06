package ju.example.training_management_system.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tero")
public class HomePageController {

    @GetMapping()
    public String getHomePage() {
        return "home-page";
    }

    @GetMapping("/about")
    public String getAboutPage(){
        return "about-page";
    }
}