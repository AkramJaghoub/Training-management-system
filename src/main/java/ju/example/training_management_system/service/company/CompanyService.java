package ju.example.training_management_system.service.company;

import jakarta.transaction.Transactional;
import ju.example.training_management_system.dto.CompanyInfoDto;
import ju.example.training_management_system.exception.UnauthorizedCompanyAccessException;
import ju.example.training_management_system.exception.UserNotFoundException;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.model.company.advertisement.Advertisement;
import ju.example.training_management_system.model.company.advertisement.Notification;
import ju.example.training_management_system.model.manage.company.CompanyInfo;
import ju.example.training_management_system.model.users.Company;
import ju.example.training_management_system.model.users.User;
import ju.example.training_management_system.repository.AdvertisementRepository;
import ju.example.training_management_system.repository.NotificationRepository;
import ju.example.training_management_system.repository.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;

import java.sql.SQLOutput;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static ju.example.training_management_system.util.Utils.*;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final UserRepository userRepository;
    private final AdvertisementRepository advertisementRepository;
    private final NotificationRepository notificationRepository;

    public Company isUserAuthorizedAsCompany(User user, String email) throws UnauthorizedCompanyAccessException {
        if (!(user instanceof Company company)) {
            throw new UnauthorizedCompanyAccessException("User with email " + email + " wasn't recognized as a company");
        }
        return company;
    }

    public ApiResponse setUpCompanyDashboard(Model model, String email) {
        try {
            User existingUser = userRepository.findByEmail(email);
            if (existingUser == null) {
                throw new UserNotFoundException("User with email " + email + " wasn't found");
            }

            Company company = isUserAuthorizedAsCompany(existingUser, email);

            String base64Image = null;
            if (company.getImage() != null) {
                byte[] decompressedImage = decompressImage(company.getImage());
                base64Image = convertToBase64(decompressedImage);
            }

            List<Advertisement> advertisements = getCompanyAdvertisementsPostedByLatest(company.getCompanyName());
            List<Notification> notifications = notificationRepository.findByUser(company);
            Collections.reverse(notifications);

            model.addAttribute("companyImage", base64Image);
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

    private List<Advertisement> getCompanyAdvertisementsPostedByLatest(String companyName) {
        return advertisementRepository.findByCompany_CompanyName(companyName).stream()
                .sorted(Comparator.comparing(Advertisement::getPostDate).reversed())
                .toList();
    }

    public ApiResponse setManageProfile(Model model, String email) {

        try {
            User existingUser = userRepository.findByEmail(email);

            if (existingUser == null) {
                throw new UserNotFoundException("User with email " + email + " wasn't found");
            }

            Company company = isUserAuthorizedAsCompany(existingUser, email);

            List<Notification> notifications = notificationRepository.findByUser(company);
            Collections.reverse(notifications);

            String base64Image = null;
            if (company.getImage() != null) {
                byte[] decompressedImage = decompressImage(company.getImage());
                base64Image = convertToBase64(decompressedImage);
            }

            model.addAttribute("email", company.getEmail());
            model.addAttribute("companyName", company.getCompanyName());
            model.addAttribute("industry", company.getIndustry());
            model.addAttribute("numOfEmployees", company.getNumOfEmployees());
            model.addAttribute("establishmentYear", company.getEstablishmentYear());
            model.addAttribute("companyImage", base64Image);
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

            CompanyInfo companyInfo = new CompanyInfo().toEntity(infoDto);

            User existingUser = userRepository.findByEmail(email);
            if (existingUser == null) {
                throw new UserNotFoundException("User with email " + email + " wasn't found");
            }

            Company company = isUserAuthorizedAsCompany(existingUser, email);

            boolean isChanged = false;

            if (companyInfo.getCompanyName() != null) {
                company.setCompanyName(companyInfo.getCompanyName());
                isChanged = true;
            }

            if (infoDto.getCompanyImage() != null) {
                byte[] imageBytes = saveImage(infoDto.getCompanyImage());
                company.setImage(imageBytes);
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

            if(!isChanged){
                throw new UnsupportedOperationException("You can't proceed with this operation, you have to at least change one field");
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
