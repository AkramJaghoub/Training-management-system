package ju.example.training_management_system.service;

import jakarta.servlet.http.HttpSession;
import ju.example.training_management_system.dto.LoginDto;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.model.users.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static ju.example.training_management_system.util.Utils.getRequiredDashboard;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final FactorAuthenticationService factorAuthenticationService;
    private final AuthenticationService authenticationService;
    private final UserService userService;

    public ApiResponse loginUser(LoginDto loginDto, HttpSession session) {
        String email = loginDto.getEmail();
        String password = loginDto.getPassword();

        if (authenticationService.isAdmin(email, password)) {
            Role role = Role.ADMIN;
            return new ApiResponse(getRequiredDashboard(role), HttpStatus.OK); // return early for admin
        }

        if (!authenticationService.isValidUser(email, password)) {
            return new ApiResponse("Wrong email or password", HttpStatus.UNAUTHORIZED);
        }

//        boolean isAuthenticated = factorAuthenticationService.isTokenPersisted(loginDto.getEmail());
//        if (!isAuthenticated) {
//            return new ApiResponse("Redirect to 2FA", HttpStatus.TEMPORARY_REDIRECT);
//        }

        Role role = userService.getUserRole(email);

        session.setAttribute("email", email);
        return new ApiResponse(getRequiredDashboard(role), HttpStatus.OK);
    }
}
