package ju.example.training_management_system.service.company.post;

import jakarta.transaction.Transactional;
import ju.example.training_management_system.dto.AdvertisementDto;
import ju.example.training_management_system.exception.PostAlreadyExistsException;
import ju.example.training_management_system.exception.StorageException;
import ju.example.training_management_system.model.company.advertisement.Advertisement;
import ju.example.training_management_system.model.users.Company;
import ju.example.training_management_system.model.users.User;
import ju.example.training_management_system.repository.AdvertisementRepository;
import ju.example.training_management_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AdsPostService {

    private final AdvertisementRepository advertisementRepository;
    private final UserRepository userRepository;

    @Transactional
    public void postAd(AdvertisementDto adDto, String email) {

        try {
            Advertisement ad = new Advertisement().toEntity(adDto);

            System.out.println(adDto + " dtoooooooo");


            String imageUrl = saveImage(adDto.getJobImage());
            ad.setImageUrl(imageUrl);
            System.out.println(ad.getImageUrl() + " sssssssssssss");

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

    private String saveImage(MultipartFile file) {
        Path rootLocation = Paths.get("src/main/resources/static/job-post/images-uploaded");

        if (file.isEmpty()) {
            return null;
        }
        try {
            String filename = System.currentTimeMillis() + "-" + file.getOriginalFilename();
            Path destinationFile = rootLocation.resolve(Paths.get(filename))
                    .normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(rootLocation.toAbsolutePath())) {
                throw new StorageException("Cannot store file outside current directory.");
            }

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            return "/job-post/images-uploaded/" + filename;
        } catch (IOException e) {
            throw new StorageException("Failed to store file.", e);
        }
    }

    public List<Advertisement> getAllAdvertisementsForCompany(String companyName) {
        return advertisementRepository.findByCompanyName(companyName);
    }
}
