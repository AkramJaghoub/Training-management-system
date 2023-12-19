package ju.example.training_management_system.service.company;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import ju.example.training_management_system.dto.CompanyInfoDto;
import ju.example.training_management_system.exception.UnauthorizedCompanyAccessException;
import ju.example.training_management_system.exception.UserNotFoundException;
import ju.example.training_management_system.model.ApiResponse;
import ju.example.training_management_system.model.manage.company.CompanyInfo;
import ju.example.training_management_system.model.users.Company;
import ju.example.training_management_system.model.users.User;
import ju.example.training_management_system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;

import static ju.example.training_management_system.util.Utils.*;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final UserRepository userRepository;

    public String getCompanyName(String email) {
        User user = userRepository.findByEmail(email);
        if (user instanceof Company company) {
            return company.getCompanyName();
        }
        return null;
    }

    @Transactional
    public ApiResponse updateCompanyDetails(@RequestBody CompanyInfoDto infoDto,
                                            String email) {
        try {

            CompanyInfo companyInfo = new CompanyInfo().toEntity(infoDto);

            System.out.println(infoDto);

            System.out.println(companyInfo + " company info");
            User existingUser = userRepository.findByEmail(email);
            if (existingUser == null) {
                throw new UserNotFoundException("User with email " + email + " wasn't found");
            }

            if (!(existingUser instanceof Company company)) {
                throw new UnauthorizedCompanyAccessException("User with email " + email + " wasn't recognized as a company");
            }

//        if (userData.containsKey("password") && userData.get("password") != null) {
//            String hashedPassword = PasswordHashingUtil.hashPassword((String) userData.get("password"));
//            company.setPassword(hashedPassword);
//        }

            if (companyInfo.getCompanyName() != null) {
                company.setCompanyName(companyInfo.getCompanyName());
            }

            if (infoDto.getCompanyImage() != null) {
                byte[] imageBytes = saveImage(infoDto.getCompanyImage());
                company.setImage(imageBytes);
            }

            if (companyInfo.getLocation() != null) {
                company.setLocation(companyInfo.getLocation());
            }

            if (companyInfo.getEstablishmentYear() != null) {
                company.setEstablishmentYear(companyInfo.getEstablishmentYear());
            }

            if (companyInfo.getPhoneNumber() != null) {
                company.setPhoneNumber(companyInfo.getPhoneNumber());
            }

            if (companyInfo.getNumOfEmployees() != null) {
                company.setNumOfEmployees(companyInfo.getNumOfEmployees());
            }

            userRepository.save(company);
            return new ApiResponse("Company details updated successfully", HttpStatus.OK);
        } catch (UserNotFoundException | UnauthorizedCompanyAccessException ex) {
            return new ApiResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    public void setManageProfile(Model model, String email) {
        User existingUser = userRepository.findByEmail(email);
        if (existingUser == null) {
            throw new UserNotFoundException("User with email " + email + " wasn't found");
        }

        if (!(existingUser instanceof Company company)) {
            throw new UnauthorizedCompanyAccessException("User with email " + email + " wasn't recognized as a company");
        }

        String base64Image = null;
        if (company.getImage() != null) {
            byte[] decompressedImage = decompressImage(company.getImage());
            base64Image = convertToBase64(decompressedImage);
        }

        model.addAttribute("email", company.getEmail());
        model.addAttribute("companyName", company.getCompanyName());
        model.addAttribute("industry", company.getIndustry());
        model.addAttribute("location", company.getLocation());
        model.addAttribute("phoneNumber", company.getPhoneNumber());
        model.addAttribute("numOfEmployees", company.getNumOfEmployees());
        model.addAttribute("establishmentYear", company.getEstablishmentYear());
        model.addAttribute("companyImage", base64Image);
    }

    public void setUpCompanyImage(Model model, String email, HttpServletResponse response){
        User existingUser = userRepository.findByEmail(email);
        if (existingUser == null) {
            throw new UserNotFoundException("User with email " + email + " wasn't found");
        }

        if (!(existingUser instanceof Company company)) {
            throw new UnauthorizedCompanyAccessException("User with email " + email + " wasn't recognized as a company");
        }

        String base64Image = null;
        if (company.getImage() != null) {
            byte[] decompressedImage = decompressImage(company.getImage());
            base64Image = convertToBase64(decompressedImage);
        }

        model.addAttribute("companyImage", base64Image);

        Cookie companyNameCookie = new Cookie("companyName", company.getCompanyName());
        companyNameCookie.setPath("/");
        companyNameCookie.setMaxAge(24 * 60 * 60 * 30);
        response.addCookie(companyNameCookie);
    }
}
