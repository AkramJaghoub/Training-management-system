package ju.example.training_management_system.service;

import ju.example.training_management_system.database.DatabaseProperties;
import ju.example.training_management_system.exception.PasswordNotMatchException;
import ju.example.training_management_system.exception.UserNotFoundException;
import ju.example.training_management_system.model.Role;
import ju.example.training_management_system.model.User;
import ju.example.training_management_system.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final DatabaseProperties databaseProperties;

    private final static Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    public boolean isValidUser(User user) {

        try {

            logger.info("trying to find user with the email {}", user.getEmail());
            User storedUser = userRepository.findByEmail(user.getEmail());

            if (storedUser == null) {
                logger.info("user with the email {} wasn't found", user.getEmail());
                throw new UserNotFoundException();
            }

            logger.info("user with the email {} was found now validating their password", user.getEmail());
            if (!storedUser.getPassword().equals(user.getPassword())) {
                logger.info("user with the email {} their password is incorrect try again", user.getEmail());
                throw new PasswordNotMatchException();
            }

            logger.info("validation for user with the email {} was successful entering user's interface...", user.getEmail());
            return true;  // user's email was found and their password is correct

        } catch (UserNotFoundException | PasswordNotMatchException ex) {
            ex.printStackTrace();
            return false; // user's email wasn't found or password is incorrect
        }
    }

    public boolean isAdmin(User user) {
        String dbUsername = databaseProperties.getDatabaseUsername();
        String dbPassword = databaseProperties.getDatabasePassword();
        return !user.getEmail().equals(dbUsername) || !user.getPassword().equals(dbPassword);
    }

    public Role getUserRole(String email){
        User user = userRepository.findByEmail(email);
        return user.getRole();
    }
}