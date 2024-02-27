package ju.example.training_management_system.repository.users;

import ju.example.training_management_system.entity.users.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
  CompanyEntity findByCompanyName(String companyName);
}
