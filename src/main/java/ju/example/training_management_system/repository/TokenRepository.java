package ju.example.training_management_system.repository;

import ju.example.training_management_system.model.PasswordResetToken;
import ju.example.training_management_system.model.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);

    void deleteByUserId(Long id);

    PasswordResetToken findByUserId(Long id);
}
