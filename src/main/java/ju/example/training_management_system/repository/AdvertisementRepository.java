package ju.example.training_management_system.repository;

import java.util.List;
import ju.example.training_management_system.entity.AdvertisementEntity;
import ju.example.training_management_system.entity.users.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdvertisementRepository extends JpaRepository<AdvertisementEntity, Long> {
  boolean existsByJobTitleAndCompany(String jobTitle, CompanyEntity company);

  List<AdvertisementEntity> findByCompany_CompanyName(String companyName);

  void deleteByCompanyId(long userId);
}
