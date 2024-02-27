package ju.example.training_management_system.entity.users;

import static ju.example.training_management_system.util.Utils.capitalizeFirstLetter;

import jakarta.persistence.Entity;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class CompanyEntity extends UserEntity {

  private String companyName;
  private String industry;
  private Integer numOfEmployees;
  private Integer establishmentYear;
  private double rating;
  private double averageRating;
  private int ratingCount;

  public CompanyEntity(String email, String password) {
    super(email, password);
  }

  public CompanyEntity build(Map<String, Object> properties) {
    CompanyEntity company = new CompanyEntity();
    company.setCompanyName(capitalizeFirstLetter((String) properties.get("companyName")));
    company.setEmail((String) properties.get("email"));
    company.setPassword((String) properties.get("password"));
    return company;
  }

  public void addRating(double rating) {
    this.averageRating = (this.averageRating * this.ratingCount + rating) / (this.ratingCount + 1);
    this.ratingCount++;
  }
}
