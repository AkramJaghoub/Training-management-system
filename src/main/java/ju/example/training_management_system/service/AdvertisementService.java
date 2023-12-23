package ju.example.training_management_system.service;

import jakarta.transaction.Transactional;
import ju.example.training_management_system.dto.AdvertisementDto;
import ju.example.training_management_system.exception.AdAlreadyExistsException;
import ju.example.training_management_system.exception.AdDoesNotExistException;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.model.company.advertisement.Advertisement;
import ju.example.training_management_system.model.users.Company;
import ju.example.training_management_system.model.users.User;
import ju.example.training_management_system.repository.AdvertisementRepository;
import ju.example.training_management_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static ju.example.training_management_system.util.Utils.isNotEqual;


@Service
@RequiredArgsConstructor
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final UserRepository userRepository;

    @Transactional
    public ApiResponse postAd(AdvertisementDto adDto, String email) {

        try {
            Advertisement ad = new Advertisement().toEntity(adDto);

//                String imageUrl = saveImage(adDto.getJobImage());
//                ad.setImageUrl(imageUrl);

            if (advertisementRepository.existsByJobTitle(ad.getJobTitle())) {
                throw new AdAlreadyExistsException("A post with the same title already exists!");
            }

            User user = userRepository.findByEmail(email);

            if (user instanceof Company company) {
                ad.setCompany(company);
            }

            ad.setPostDate(LocalDateTime.now());

            advertisementRepository.save(ad);
            return new ApiResponse("advertisement was saved successfully", HttpStatus.CREATED);

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

            if (isNotEqual(adDto.getJobTitle(), existingAd.getJobTitle()) &&
                    advertisementRepository.existsByJobTitle(adDto.getJobTitle())) {
                throw new AdAlreadyExistsException("An advertisement with the same title already exists");
            }

//            String imageUrl = saveImage(adDto.getJobImage());

//            existingAd.setImageUrl(imageUrl);
            existingAd.setJobTitle(adDto.getJobTitle());
            existingAd.setInternsRequired(adDto.getInternsRequired());
            existingAd.setJobDuration(adDto.getJobDuration());
            existingAd.setDescription(adDto.getDescription());
            existingAd.setJobType(adDto.getJobType());
            existingAd.setCountry(adDto.getCountry());
            existingAd.setCity(adDto.getCity());
            existingAd.setWorkMode(adDto.getWorkMode());

            User user = userRepository.findByEmail(email);

            if (user instanceof Company company) {
                existingAd.setCompany(company);
            }

            advertisementRepository.save(existingAd); // updated advertisement
            return new ApiResponse("advertisement was updated successfully", HttpStatus.CREATED);
        } catch (AdAlreadyExistsException | AdDoesNotExistException ex) {
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