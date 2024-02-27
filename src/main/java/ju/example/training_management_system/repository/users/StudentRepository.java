package ju.example.training_management_system.repository.users;

import ju.example.training_management_system.entity.users.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<StudentEntity, Long> {}
