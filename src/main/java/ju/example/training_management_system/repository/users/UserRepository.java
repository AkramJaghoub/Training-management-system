package ju.example.training_management_system.repository.users;

import ju.example.training_management_system.entity.users.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
  UserEntity findByEmail(String email);

  boolean existsByEmail(String email);
}
