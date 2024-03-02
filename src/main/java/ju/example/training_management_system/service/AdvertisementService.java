package ju.example.training_management_system.service;

import static ju.example.training_management_system.model.PostStatus.PENDING;
import static ju.example.training_management_system.util.Utils.*;

import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import ju.example.training_management_system.entity.AdvertisementEntity;
import ju.example.training_management_system.entity.NotificationEntity;
import ju.example.training_management_system.entity.users.CompanyEntity;
import ju.example.training_management_system.entity.users.UserEntity;
import ju.example.training_management_system.exception.AdAlreadyExistsException;
import ju.example.training_management_system.exception.AdDoesNotExistException;
import ju.example.training_management_system.exception.UnauthorizedCompanyAccessException;
import ju.example.training_management_system.exception.UserNotFoundException;
import ju.example.training_management_system.model.Advertisement;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.repository.AdvertisementRepository;
import ju.example.training_management_system.repository.NotificationRepository;
import ju.example.training_management_system.repository.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
@RequiredArgsConstructor
public class AdvertisementService {

  private final AdvertisementRepository advertisementRepository;
  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;

  public CompanyEntity isUserAuthorizedAsCompany(UserEntity user, String email)
      throws UnauthorizedCompanyAccessException {
    if (!(user instanceof CompanyEntity company)) {
      throw new UnauthorizedCompanyAccessException(
          "User with email " + email + " wasn't recognized as a company");
    }
    return company;
  }

  public ApiResponse setUpPostAdsPage(Model model, String email) {
    try {
      UserEntity existingUser = userRepository.findByEmail(email);
      if (existingUser == null) {
        throw new UserNotFoundException("User with email " + email + " wasn't found");
      }

      CompanyEntity company = isUserAuthorizedAsCompany(existingUser, email);

      List<NotificationEntity> notifications = notificationRepository.findByUser(company);
      Collections.reverse(notifications);

      model.addAttribute("companyImage", company.getImageUrl());
      model.addAttribute("companyName", company.getCompanyName());
      model.addAttribute("notifications", notifications);

      return new ApiResponse("Set up was correctly done", HttpStatus.OK);
    } catch (UserNotFoundException ex) {
      return new ApiResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (UnauthorizedCompanyAccessException ex) {
      return new ApiResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }
  }

  @Transactional
  public ApiResponse postAd(Advertisement advertisement, String email) {
    try {
      AdvertisementEntity ad = new AdvertisementEntity().toEntity(advertisement);

      String imageUrl = saveImage(advertisement.getJobImage(), "company");
      ad.setImageUrl(imageUrl);

      UserEntity user = userRepository.findByEmail(email);
      if (!(user instanceof CompanyEntity company)) {
        throw new UnauthorizedCompanyAccessException(
            "User with email " + email + " wasn't recognized as a company");
      }

      ad.setCompany(company);
      if (advertisementRepository.existsByJobTitleAndCompany(ad.getJobTitle(), company)) {
        throw new AdAlreadyExistsException("A post with the same title already exists!");
      }

      advertisementRepository.save(ad);
      return new ApiResponse(
          "Advertisement with job title [" + ad.getJobTitle() + "] was posted successfully",
          HttpStatus.CREATED);
    } catch (AdAlreadyExistsException ex) {
      return new ApiResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (UnauthorizedCompanyAccessException ex) {
      return new ApiResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }
  }

  @Transactional
  public ApiResponse updateAd(Advertisement advertisement, String email) {
    try {
      AdvertisementEntity existingAd =
          advertisementRepository
              .findById(advertisement.getId())
              .orElseThrow(() -> new AdDoesNotExistException("Advertisement not found"));

      UserEntity user = userRepository.findByEmail(email);
      if (!(user instanceof CompanyEntity company)) {
        throw new UnauthorizedCompanyAccessException(
            "User with email " + email + " wasn't recognized as a company");
      }
      existingAd.setCompany(company);

      if (!Objects.equals(advertisement.getJobTitle(), existingAd.getJobTitle())
          && advertisementRepository.existsByJobTitleAndCompany(
              advertisement.getJobTitle(), company)) {
        throw new AdAlreadyExistsException("An advertisement with the same title already exists!");
      }

      String imageUrl = saveImage(advertisement.getJobImage(), "");

      existingAd.setImageUrl(imageUrl);
      existingAd.setJobTitle(capitalizeFirstLetter(advertisement.getJobTitle()));
      existingAd.setInternsRequired(advertisement.getInternsRequired());
      existingAd.setJobDuration(advertisement.getJobDuration());
      existingAd.setDescription(advertisement.getDescription());
      existingAd.setJobType(advertisement.getJobType());
      existingAd.setCountry(advertisement.getCountry());
      existingAd.setCity(advertisement.getCity());
      existingAd.setWorkMode(advertisement.getWorkMode());
      existingAd.setApplicationLink(advertisement.getApplicationLink());
      existingAd.setPostStatus(PENDING);

      advertisementRepository.save(existingAd); // updated advertisement
      return new ApiResponse(
          "Advertisement with job title ["
              + existingAd.getJobTitle()
              + "] was updated successfully",
          HttpStatus.CREATED);
    } catch (AdAlreadyExistsException | AdDoesNotExistException ex) {
      return new ApiResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (UnauthorizedCompanyAccessException ex) {
      return new ApiResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }
  }

  public ApiResponse deleteAd(long adId, String email) {
    try {
      AdvertisementEntity advertisement =
          advertisementRepository
              .findById(adId)
              .orElseThrow(
                  () ->
                      new AdDoesNotExistException(
                          "Advertisement with id [" + adId + "] does not exist"));

      UserEntity user = userRepository.findByEmail(email);
      if (!(user instanceof CompanyEntity)) {
        throw new UnauthorizedCompanyAccessException(
            "User with email " + email + " wasn't recognized as a company");
      }

      advertisementRepository.deleteById(adId);
      return new ApiResponse(
          "Advertisement with job title ["
              + advertisement.getJobTitle()
              + "] was delete successfully",
          HttpStatus.OK);
    } catch (AdDoesNotExistException ex) {
      return new ApiResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (UnauthorizedCompanyAccessException ex) {
      return new ApiResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }
  }
}
