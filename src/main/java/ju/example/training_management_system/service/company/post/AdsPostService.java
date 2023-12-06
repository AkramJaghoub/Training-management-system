package ju.example.training_management_system.service.company.post;

import jakarta.transaction.Transactional;
import ju.example.training_management_system.dto.AdvertisementDto;
import ju.example.training_management_system.exception.PostAlreadyExistsException;
import ju.example.training_management_system.model.company.advertisement.Advertisement;
import ju.example.training_management_system.model.users.Company;
import ju.example.training_management_system.model.users.User;
import ju.example.training_management_system.repository.AdvertisementRepository;
import ju.example.training_management_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ListResourceBundle;

@Service
@RequiredArgsConstructor
public class AdsPostService {

    private final AdvertisementRepository advertisementRepository;
    private final UserRepository userRepository;

    @Transactional
    public void postAd(AdvertisementDto adDto, String email) {

        try {
            Advertisement ad = new Advertisement().toEntity(adDto);

            if (advertisementRepository.existsByJobTitle(ad.getJobTitle())) {
                throw new PostAlreadyExistsException("A post with the same title already exists!");
            }

            User user = userRepository.findByEmail(email);

            if(user instanceof Company company) {
                ad.setCompany(company);
            }

            advertisementRepository.save(ad);
        }catch (PostAlreadyExistsException ex){
            ex.printStackTrace();
        }
    }

    public List<Advertisement> getAllAdvertisements() {
        return advertisementRepository.findAll();
    }
}
