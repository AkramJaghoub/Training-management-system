package ju.example.training_management_system.repository;

import ju.example.training_management_system.entity.PasswordResetTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<PasswordResetTokenEntity, Long> {
  PasswordResetTokenEntity findByToken(String token);

  void deleteByUserId(Long id);

  PasswordResetTokenEntity findByUserId(Long id);
}
