package ju.example.training_management_system.model.users;

import java.util.Map;
import ju.example.training_management_system.exception.UserNotFoundException;

public class UserFactory {
  public static User createUser(Role role, Map<String, Object> properties) {

    try {
      switch (role) {
        case STUDENT -> {
          return new Student().build(properties);
        }

        case COMPANY -> {
          return new Company().build(properties);
        }

        default -> throw new UserNotFoundException("User wasn't found");
      }
    } catch (UserNotFoundException ex) {
      return new User();
    }
  }
}
