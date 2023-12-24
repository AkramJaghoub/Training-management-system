package ju.example.training_management_system.repository.users;

import ju.example.training_management_system.model.users.Company;
import ju.example.training_management_system.model.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
}
