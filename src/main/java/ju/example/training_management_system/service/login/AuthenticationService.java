package ju.example.training_management_system.service.login;

import ju.example.training_management_system.database.DatabaseProperties;
import ju.example.training_management_system.exception.PasswordNotMatchException;
import ju.example.training_management_system.exception.UserNotFoundException;
import ju.example.training_management_system.model.users.User;
import ju.example.training_management_system.repository.users.UserRepository;
import ju.example.training_management_system.util.PasswordHashingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
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
        throw new UserNotFoundException("User with email " + email + " wasn't found");
      }

      log.info("user with the email {} was found now validating their password", email);

      String hashedPassword = PasswordHashingUtil.hashPassword(password);
      if (!storedUser.getPassword().equals(hashedPassword)) {
        log.info("user with the email {} their password is incorrect try again", email);
        throw new PasswordNotMatchException("Current password is invalid");
      }

      log.info(
          "validation for user with the email {} was successful entering user's interface...",
          email);
      return true; // user's email was found and their password is correct

    } catch (UserNotFoundException | PasswordNotMatchException ex) {
      log.info("error while authenticating the user with email {}", email);
      return false; // user's email wasn't found or password is incorrect
    }
  }

  public boolean isAdmin(String email, String password) {
    String dbUsername = databaseProperties.getDatabaseUsername();
    String dbPassword = databaseProperties.getDatabasePassword();
    return email.equals(dbUsername) && password.equals(dbPassword);
  }
}
