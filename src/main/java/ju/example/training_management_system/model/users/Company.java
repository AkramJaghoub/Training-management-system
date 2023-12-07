package ju.example.training_management_system.model.users;

import jakarta.persistence.Entity;
import java.util.Map;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Company extends User {

  private String name;
  private String industry;

  public Company(String email, String password) {
    super(email, password);
  }

  public Company build(Map<String, Object> properties) {
    Company company = new Company();
    company.setName((String) properties.get("name"));
    company.setEmail((String) properties.get("email"));
    company.setIndustry((String) properties.get("industry"));
    company.setPassword((String) properties.get("password"));
    return company;
  }
}
