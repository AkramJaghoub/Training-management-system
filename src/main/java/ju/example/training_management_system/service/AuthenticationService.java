package ju.example.training_management_system.service;

import ju.example.training_management_system.database.DatabaseProperties;
import ju.example.training_management_system.exception.PasswordNotMatchException;
import ju.example.training_management_system.exception.UserNotFoundException;
import ju.example.training_management_system.model.Role;
import ju.example.training_management_system.model.users.User;
import ju.example.training_management_system.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final DatabaseProperties databaseProperties;

    public boolean isValidUser(String email, String password) {

        try {

            log.info("trying to find user with the email {}", email);
            User storedUser = userRepository.findByEmail(email);

            if (storedUser == null) {
                log.info("user with the email {} wasn't found", email);
                throw new UserNotFoundException();
            }

            log.info("user with the email {} was found now validating their password", email);
            if (!storedUser.getPassword().equals(password)) {
                log.info("user with the email {} their password is incorrect try again", email);
                throw new PasswordNotMatchException();
            }

            log.info("validation for user with the email {} was successful entering user's interface...", email);
            return true;  // user's email was found and their password is correct

        } catch (UserNotFoundException | PasswordNotMatchException ex) {
            ex.printStackTrace();
            return false; // user's email wasn't found or password is incorrect
        }
    }

    public boolean isAdmin(String email, String password) {
        String dbUsername = databaseProperties.getDatabaseUsername();
        String dbPassword = databaseProperties.getDatabasePassword();
        return email.equals(dbUsername) && password.equals(dbPassword);
    }

    public Role getUserRole(String email){
        User user = userRepository.findByEmail(email);
        return user.getRole();
    }
}