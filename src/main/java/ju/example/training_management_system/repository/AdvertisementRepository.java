package ju.example.training_management_system.repository;

import ju.example.training_management_system.model.company.advertisement.Advertisement;
import ju.example.training_management_system.model.users.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {
    boolean existsByJobTitleAndCompany(String jobTitle, Company company);
    List<Advertisement> findByCompany_CompanyName(String companyName);
    void deleteByCompanyId(long userId);
}