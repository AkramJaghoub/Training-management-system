package ju.example.training_management_system.model.users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Company extends User {

    private String companyName;

    private String industry;

    private String phoneNumber;

    private String location;

    private Integer numOfEmployees;

    @Column(columnDefinition = "LONGBLOB")
    private byte[] image;

    private Integer establishmentYear;

    public Company(String email, String password) {
        super(email, password);
    }

    public Company build(Map<String, Object> properties) {
        Company company = new Company();
        company.setCompanyName((String) properties.get("companyName"));
        company.setEmail((String) properties.get("email"));
        company.setPhoneNumber((String) properties.get("phoneNumber"));
        company.setPassword((String) properties.get("password"));
        return company;
    }
}
