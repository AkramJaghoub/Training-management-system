package ju.example.training_management_system.repository;

import java.util.List;
import ju.example.training_management_system.entity.advertisement.NotificationEntity;
import ju.example.training_management_system.entity.users.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
  List<NotificationEntity> findByUser(UserEntity user);
}
