package ju.example.training_management_system.service;

import jakarta.servlet.http.HttpSession;
import ju.example.training_management_system.dto.LoginDto;
import ju.example.training_management_system.model.users.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    public String loginUser(LoginDto loginDto, HttpSession session) {

        String email = loginDto.getEmail();
        String password = loginDto.getPassword();

        if (authenticationService.isAdmin(email, password)) {
            Role role = Role.ADMIN;
            return "redirect:/" + role.toString().toLowerCase() + "/dashboard"; // return early for admin
        }

        Role role = userService.getUserRole(email);

        if (!authenticationService.isValidUser(email, password)) {
            return "invalid credentials";
        }

        session.setAttribute("email", email);
        return "redirect:/" + role.toString().toLowerCase() + "/dashboard";

    }
}
