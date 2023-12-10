package ju.example.training_management_system.service;

import jakarta.transaction.Transactional;
import ju.example.training_management_system.exception.UserAlreadyExistException;
import ju.example.training_management_system.model.users.Role;
import ju.example.training_management_system.model.users.User;
import ju.example.training_management_system.model.users.UserFactory;
import ju.example.training_management_system.repository.UserRepository;
import ju.example.training_management_system.util.PasswordHashingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserRepository userRepository;

    @Transactional
    public String registerUser(Map<String, Object> userData) {
        String roleStr = (String) userData.get("role");
        Role role = Role.toRole(roleStr);
        User user = UserFactory.createUser(role, userData);
        String hashedPassword = PasswordHashingUtil.hashPassword(user.getPassword());
        user.setPassword(hashedPassword);
        try {
            if (userRepository.existsByEmail(user.getEmail())) {
                throw new UserAlreadyExistException();
            }
            user.setRole(role);
            userRepository.save(user);
            return "user has been saved successfully";
        } catch (UserAlreadyExistException ex) {
            return "user email already exists";
        }
    }
}
