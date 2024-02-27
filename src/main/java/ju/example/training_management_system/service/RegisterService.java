package ju.example.training_management_system.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import java.util.Map;
import ju.example.training_management_system.entity.users.UserEntity;
import ju.example.training_management_system.exception.UserAlreadyExistException;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.model.Role;
import ju.example.training_management_system.model.UserFactory;
import ju.example.training_management_system.repository.users.UserRepository;
import ju.example.training_management_system.util.PasswordHashingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterService {

  private final UserRepository userRepository;
  private final HttpServletRequest request;

  @Transactional
  public ApiResponse registerUser(Map<String, Object> userData) {
    String roleStr = (String) userData.get("role");
    Role role = Role.toRole(roleStr);
    UserEntity user = UserFactory.createUser(role, userData);
    String hashedPassword = PasswordHashingUtil.hashPassword(user.getPassword());
    user.setPassword(hashedPassword);
    try {
      if (userRepository.existsByEmail(user.getEmail())) {
        throw new UserAlreadyExistException("user email already exists");
      }
      user.setRole(role);
      userRepository.save(user);

      HttpSession session = request.getSession();
      session.setAttribute("email", user.getEmail());
      return new ApiResponse(
          "user with [" + user.getId() + "] has been saved successfully", HttpStatus.OK);
    } catch (UserAlreadyExistException ex) {
      return new ApiResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
  }
}
