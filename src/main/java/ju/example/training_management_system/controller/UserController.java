package ju.example.training_management_system.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class UserController {

    private final UserService userService;
    private final HttpServletRequest request;

    @PutMapping("change-password")
    public ResponseEntity<?> changePassword(@RequestHeader("currentPassword") String currentPassword,
                                            @RequestHeader("newPassword") String newPassword) {
        HttpSession httpSession = request.getSession();
        String email = (String) httpSession.getAttribute("email");

        if (email != null) {
            ApiResponse apiResponse = userService.changePassword(email, currentPassword, newPassword);
            return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/login");
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }
}
