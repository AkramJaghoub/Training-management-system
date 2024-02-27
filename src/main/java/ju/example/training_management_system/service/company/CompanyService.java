package ju.example.training_management_system.service.company;

import static ju.example.training_management_system.util.Utils.*;

import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import ju.example.training_management_system.dto.CompanyInfoDto;
import ju.example.training_management_system.entity.advertisement.AdvertisementEntity;
import ju.example.training_management_system.entity.advertisement.NotificationEntity;
import ju.example.training_management_system.entity.users.CompanyEntity;
import ju.example.training_management_system.entity.users.UserEntity;
import ju.example.training_management_system.exception.UnauthorizedCompanyAccessException;
import ju.example.training_management_system.exception.UserNotFoundException;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.model.CompanyInfo;
import ju.example.training_management_system.repository.AdvertisementRepository;
import ju.example.training_management_system.repository.NotificationRepository;
import ju.example.training_management_system.repository.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class CompanyService {

  private final UserRepository userRepository;
  private final AdvertisementRepository advertisementRepository;
  private final NotificationRepository notificationRepository;

  public CompanyEntity isUserAuthorizedAsCompany(UserEntity user, String email)
      throws UnauthorizedCompanyAccessException {
    if (!(user instanceof CompanyEntity company)) {
      throw new UnauthorizedCompanyAccessException(
          "UserEntity with email " + email + " wasn't recognized as a company");
    }
    return company;
  }

  public ApiResponse setUpCompanyDashboard(Model model, String email) {
    try {
      UserEntity existingUser = userRepository.findByEmail(email);
      if (existingUser == null) {
        throw new UserNotFoundException("UserEntity with email " + email + " wasn't found");
      }

      CompanyEntity company = isUserAuthorizedAsCompany(existingUser, email);

      List<AdvertisementEntity> advertisements =
          getCompanyAdvertisementsPostedByLatest(company.getCompanyName());
      List<NotificationEntity> notifications = notificationRepository.findByUser(company);
      Collections.reverse(notifications);

      model.addAttribute("companyImage", company.getImageUrl());
      model.addAttribute("advertisements", advertisements);
      model.addAttribute("companyName", company.getCompanyName());
      model.addAttribute("notifications", notifications);

      return new ApiResponse("Set up was correctly done", HttpStatus.OK);
    } catch (UserNotFoundException ex) {
      return new ApiResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (UnauthorizedCompanyAccessException ex) {
      return new ApiResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }
  }

  private List<AdvertisementEntity> getCompanyAdvertisementsPostedByLatest(String companyName) {
    return advertisementRepository.findByCompany_CompanyName(companyName).stream()
        .sorted(Comparator.comparing(AdvertisementEntity::getPostDate).reversed())
        .toList();
  }

  public ApiResponse setManageProfile(Model model, String email) {

    try {
      UserEntity existingUser = userRepository.findByEmail(email);

      if (existingUser == null) {
        throw new UserNotFoundException("UserEntity with email " + email + " wasn't found");
      }

      CompanyEntity company = isUserAuthorizedAsCompany(existingUser, email);

      List<NotificationEntity> notifications = notificationRepository.findByUser(company);
      Collections.reverse(notifications);

      model.addAttribute("email", company.getEmail());
      model.addAttribute("companyName", company.getCompanyName());
      model.addAttribute("industry", company.getIndustry());
      model.addAttribute("numOfEmployees", company.getNumOfEmployees());
      model.addAttribute("establishmentYear", company.getEstablishmentYear());
      model.addAttribute("companyImage", company.getImageUrl());
      model.addAttribute("notifications", notifications);

      return new ApiResponse("Set up was correctly done", HttpStatus.OK);
    } catch (UserNotFoundException ex) {
      return new ApiResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (UnauthorizedCompanyAccessException ex) {
      return new ApiResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }
  }

  @Transactional
  public ApiResponse updateCompanyDetails(@RequestBody CompanyInfoDto infoDto, String email) {
    try {

      CompanyInfo companyInfo = new CompanyInfo().toModel(infoDto);

      UserEntity existingUser = userRepository.findByEmail(email);
      if (existingUser == null) {
        throw new UserNotFoundException("User with email " + email + " wasn't found");
      }

      CompanyEntity company = isUserAuthorizedAsCompany(existingUser, email);

      boolean isChanged = false;

      if (companyInfo.getCompanyName() != null) {
        company.setCompanyName(companyInfo.getCompanyName());
        isChanged = true;
      }

      if (infoDto.getCompanyImage() != null) {
        String imageUrl = saveImage(infoDto.getCompanyImage(), String.valueOf(company.getId()));
        company.setImageUrl(imageUrl);
        isChanged = true;
      }

      if (companyInfo.getEstablishmentYear() != null) {
        company.setEstablishmentYear(companyInfo.getEstablishmentYear());
        isChanged = true;
      }

      if (companyInfo.getNumOfEmployees() != null) {
        company.setNumOfEmployees(companyInfo.getNumOfEmployees());
        isChanged = true;
      }

      if (!isChanged) {
        throw new UnsupportedOperationException(
            "You can't proceed with this operation, you have to at least change one field");
      }

      userRepository.save(company);
      return new ApiResponse("Company details updated successfully", HttpStatus.OK);

    } catch (UserNotFoundException | UnsupportedOperationException ex) {
      return new ApiResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (UnauthorizedCompanyAccessException ex) {
      return new ApiResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }
  }
}
