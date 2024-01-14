package ju.example.training_management_system.controller;

import static java.util.Objects.isNull;
import static ju.example.training_management_system.util.Utils.redirectToPage;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.service.UserService;
import lombok.RequiredArgsConstructor;
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
  public ResponseEntity<?> changePassword(
      @RequestHeader("currentPassword") String currentPassword,
      @RequestHeader("newPassword") String newPassword) {
    HttpSession httpSession = request.getSession();
    String email = (String) httpSession.getAttribute("email");
    if (isNull(email)) {
      redirectToPage("/login");
    }

    ApiResponse response = userService.changePassword(email, currentPassword, newPassword);
    return response.getStatus() == HttpStatus.OK
        ? ResponseEntity.status(response.getStatus()).body(response)
        : redirectToPage("/login");
  }
}
