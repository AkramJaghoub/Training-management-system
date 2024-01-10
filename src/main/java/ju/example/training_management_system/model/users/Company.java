package ju.example.training_management_system.model.users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

import static ju.example.training_management_system.util.Utils.capitalizeFirstLetter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Company extends User {

    private String companyName;
    private String industry;
    private Integer numOfEmployees;
    private Integer establishmentYear;

    public Company(String email, String password) {
        super(email, password);
    }

    public Company build(Map<String, Object> properties) {
        Company company = new Company();
        company.setCompanyName(capitalizeFirstLetter((String) properties.get("companyName")));
        company.setEmail((String) properties.get("email"));
        company.setPassword((String) properties.get("password"));
        company.setJoinDate(LocalDateTime.now());
        return company;
    }
}
