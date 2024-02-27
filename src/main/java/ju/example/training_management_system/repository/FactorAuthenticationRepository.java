package ju.example.training_management_system.repository;

import java.util.List;
import ju.example.training_management_system.entity.TwoFactorAuthenticationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FactorAuthenticationRepository
    extends JpaRepository<TwoFactorAuthenticationEntity, Long> {
  List<TwoFactorAuthenticationEntity> findAllByUserId(Long id);
}
