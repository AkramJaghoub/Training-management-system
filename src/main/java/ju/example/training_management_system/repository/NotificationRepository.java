package ju.example.training_management_system.repository;

import ju.example.training_management_system.model.company.advertisement.Notification;
import ju.example.training_management_system.model.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser(User user);
}
