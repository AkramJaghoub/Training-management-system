package ju.example.training_management_system.model.users;

import ju.example.training_management_system.exception.UserNotFoundException;
import ju.example.training_management_system.model.Role;

import java.util.Map;

public class UserFactory {
    public static User createUser(Role role, Map<String, Object> properties) {
       switch (role){
            case STUDENT -> {
                return new Student().build(properties);
            }
            case COMPANY -> {
                return new Company().build(properties);
            }
       }
       return null;
    }
}
