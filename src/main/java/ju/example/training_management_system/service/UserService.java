package ju.example.training_management_system.service;

import ju.example.training_management_system.exception.PasswordNotMatchException;
import ju.example.training_management_system.exception.UserNotFoundException;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.model.users.Role;
import ju.example.training_management_system.model.users.User;
import ju.example.training_management_system.repository.users.UserRepository;
import ju.example.training_management_system.util.PasswordHashingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Role getUserRole(String email) {
        User user = userRepository.findByEmail(email);
        return user.getRole();
    }

    public ApiResponse changePassword(String email, String currentPassword, String newPassword) {
        try {
            User user = userRepository.findByEmail(email);
            if (user == null) {
                throw new UserNotFoundException("User with email [" + email + "] was not found");
            }

            String currentHashedPassword = PasswordHashingUtil.hashPassword(currentPassword);
            if (!Objects.equals(currentHashedPassword, user.getPassword())) {
                throw new PasswordNotMatchException("Current password is incorrect");
            }

            String newHashedPassword = PasswordHashingUtil.hashPassword(newPassword);

            user.setPassword(newHashedPassword);
            userRepository.save(user);
            return new ApiResponse("User with email [" + email + "] password was changed successfully", HttpStatus.OK);
        } catch (UserNotFoundException | PasswordNotMatchException ex) {
            return new ApiResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
