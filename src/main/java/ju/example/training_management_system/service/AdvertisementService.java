package ju.example.training_management_system.service;

import jakarta.transaction.Transactional;
import ju.example.training_management_system.dto.AdvertisementDto;
import ju.example.training_management_system.exception.AdAlreadyExistsException;
import ju.example.training_management_system.exception.AdDoesNotExistException;
import ju.example.training_management_system.exception.UnauthorizedCompanyAccessException;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.model.company.advertisement.Advertisement;
import ju.example.training_management_system.model.users.Company;
import ju.example.training_management_system.model.users.User;
import ju.example.training_management_system.repository.AdvertisementRepository;
import ju.example.training_management_system.repository.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

import static ju.example.training_management_system.model.company.advertisement.JobStatus.PENDING;
import static ju.example.training_management_system.util.Utils.isNotEqual;
import static ju.example.training_management_system.util.Utils.saveImage;


@Service
@RequiredArgsConstructor
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final UserRepository userRepository;

    @Transactional
    public ApiResponse postAd(AdvertisementDto adDto, String email) {
        try {
            Advertisement ad = new Advertisement().toEntity(adDto);

            byte[] imageBytes = saveImage(adDto.getJobImage());
            ad.setImage(imageBytes);

            User user = userRepository.findByEmail(email);
            if (!(user instanceof Company company)) {
                throw new UnauthorizedCompanyAccessException("User with email " + email + " wasn't recognized as a company");
            }

            ad.setCompany(company);
            if (advertisementRepository.existsByJobTitleAndCompany(ad.getJobTitle(), company)) {
                throw new AdAlreadyExistsException("A post with the same title already exists!");
            }

            advertisementRepository.save(ad);
            return new ApiResponse("Advertisement was saved successfully", HttpStatus.CREATED);
        } catch (AdAlreadyExistsException ex) {
            return new ApiResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public List<Advertisement> getAllAdvertisementsForCompany(String companyName) {
        return advertisementRepository.findByCompany_CompanyName(companyName);
    }

    @Transactional
    public ApiResponse updateAd(AdvertisementDto adDto, String email) {
        try {
            Advertisement existingAd = advertisementRepository.findById(adDto.getId())
                    .orElseThrow(() -> new AdDoesNotExistException("Advertisement not found"));

            User user = userRepository.findByEmail(email);
            if (!(user instanceof Company company)) {
                throw new UnauthorizedCompanyAccessException("User with email " + email + " wasn't recognized as a company");
            }
            existingAd.setCompany(company);

            if (isNotEqual(adDto.getJobTitle(), existingAd.getJobTitle()) &&
                    advertisementRepository.existsByJobTitleAndCompany(adDto.getJobTitle(), company)) {
                throw new AdAlreadyExistsException("An advertisement with the same title already exists!");
            }

            byte[] imageBytes = saveImage(adDto.getJobImage());

            existingAd.setImage(imageBytes);
            existingAd.setJobTitle(adDto.getJobTitle());
            existingAd.setInternsRequired(adDto.getInternsRequired());
            existingAd.setJobDuration(adDto.getJobDuration());
            existingAd.setDescription(adDto.getDescription());
            existingAd.setJobType(adDto.getJobType());
            existingAd.setCountry(adDto.getCountry());
            existingAd.setCity(adDto.getCity());
            existingAd.setWorkMode(adDto.getWorkMode());
            existingAd.setJobStatus(PENDING);

            advertisementRepository.save(existingAd); // updated advertisement
            return new ApiResponse("Advertisement was updated successfully", HttpStatus.CREATED);
        } catch (AdAlreadyExistsException | AdDoesNotExistException | UnauthorizedCompanyAccessException ex) {
            return new ApiResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public void deleteAd(String companyName, String position) {
        List<Advertisement> advertisements = advertisementRepository.findByCompany_CompanyName(companyName);
        long adId = 0;
        for (Advertisement ad : advertisements) {
            if (ad.getJobTitle().equals(position)) {
                adId = ad.getId();
                break;
            }
        }
        advertisementRepository.deleteById(adId);
    }
}