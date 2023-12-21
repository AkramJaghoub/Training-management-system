package ju.example.training_management_system.repository;

import ju.example.training_management_system.model.TwoFactorAuthentication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FactorAuthenticationRepository extends JpaRepository<TwoFactorAuthentication, Long> {
    List<TwoFactorAuthentication> findAllByUserId(Long id);
}
