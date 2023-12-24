package ju.example.training_management_system.service;

import ju.example.training_management_system.model.users.Role;
import ju.example.training_management_system.model.users.User;
import ju.example.training_management_system.repository.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Role getUserRole(String email) {
        User user = userRepository.findByEmail(email);
        return user.getRole();
    }
}
