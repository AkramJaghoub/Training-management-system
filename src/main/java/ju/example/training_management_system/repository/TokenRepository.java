package ju.example.training_management_system.repository;

import ju.example.training_management_system.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<PasswordResetToken, Integer> {
  PasswordResetToken findByToken(String token);
}
