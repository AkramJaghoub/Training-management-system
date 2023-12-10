package ju.example.training_management_system.service;

import jakarta.servlet.http.HttpSession;
import ju.example.training_management_system.dto.LoginDto;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.model.users.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AuthenticationService authenticationService;
    private final UserService userService;

    public ApiResponse loginUser(LoginDto loginDto, HttpSession session) {

        String email = loginDto.getEmail();
        String password = loginDto.getPassword();

        if (authenticationService.isAdmin(email, password)) {
            Role role = Role.ADMIN;
            return new ApiResponse(
                    role.toString().toLowerCase() + "/dashboard", HttpStatus.OK); // return early for admin
        }

        Role role = userService.getUserRole(email);

        if (!authenticationService.isValidUser(email, password)) {
            return new ApiResponse("Wrong email or password", HttpStatus.BAD_REQUEST);
        }

        session.setAttribute("email", email);
        return new ApiResponse(role.toString().toLowerCase() + "/dashboard", HttpStatus.OK);
    }
}
