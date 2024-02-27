package ju.example.training_management_system.model;

import java.util.Map;
import ju.example.training_management_system.entity.users.CompanyEntity;
import ju.example.training_management_system.entity.users.StudentEntity;
import ju.example.training_management_system.entity.users.UserEntity;
import ju.example.training_management_system.exception.UserNotFoundException;

public class UserFactory {
  public static UserEntity createUser(Role role, Map<String, Object> properties) {

    try {
      switch (role) {
        case STUDENT -> {
          return new StudentEntity().build(properties);
        }

        case COMPANY -> {
          return new CompanyEntity().build(properties);
        }

        default -> throw new UserNotFoundException("UserEntity wasn't found");
      }
    } catch (UserNotFoundException ex) {
      return new UserEntity();
    }
  }
}
