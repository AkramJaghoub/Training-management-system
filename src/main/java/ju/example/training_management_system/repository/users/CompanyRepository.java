package ju.example.training_management_system.repository.users;

import ju.example.training_management_system.model.users.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Company findByCompanyName(String companyName);
}
