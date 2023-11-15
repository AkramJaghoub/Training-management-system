package ju.example.training_management_system.service.company;

import jakarta.transaction.Transactional;
import ju.example.training_management_system.model.users.Company;
import ju.example.training_management_system.model.users.User;
import ju.example.training_management_system.repository.UserRepository;
import ju.example.training_management_system.util.PasswordHashingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final UserRepository userRepository;

    public String getCompanyName(String email){
        User user = userRepository.findByEmail(email);
        if(user instanceof Company company) {
            System.out.println(company.getName());
            return company.getName();
        }
        return null;
    }

    @Transactional
    public String updateCompanyDetails(@RequestBody Map<String, Object> userData) {

        User existingUser = userRepository.findByEmail((String) userData.get("email"));
        if (existingUser == null) {
            return "user not found";
        }

        if (!(existingUser instanceof Company company)) {
            return "user is not a company";
        }

        if (userData.containsKey("password") && userData.get("password") != null) {
            String hashedPassword = PasswordHashingUtil.hashPassword((String) userData.get("password"));
            company.setPassword(hashedPassword);
        }

        if (userData.containsKey("name")) {
            company.setName((String) userData.get("name"));
        }

        if (userData.containsKey("industry")) {
            company.setIndustry((String) userData.get("industry"));
        }

        try {
            userRepository.save(company);
            return "Company details updated successfully";
        } catch (Exception ex) {
            return "Error updating company details: " + ex.getMessage();
        }
    }
}
