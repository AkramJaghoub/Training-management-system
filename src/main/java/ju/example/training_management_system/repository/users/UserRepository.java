package ju.example.training_management_system.repository.users;

import ju.example.training_management_system.model.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  User findByEmail(String email);

  boolean existsByEmail(String email);
}
