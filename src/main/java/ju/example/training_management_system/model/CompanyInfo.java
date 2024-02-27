package ju.example.training_management_system.model;

import ju.example.training_management_system.dto.CompanyInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyInfo {
  private String email;
  private String companyName;
  private String industry;
  private String phoneNumber;
  private String location;
  private Integer numOfEmployees;
  private Integer establishmentYear;

  public CompanyInfo toModel(CompanyInfoDto infoDto) {
    return CompanyInfo.builder()
        .email(infoDto.getEmail())
        .companyName(infoDto.getCompanyName())
        .industry(infoDto.getIndustry())
        .numOfEmployees(infoDto.getNumOfEmployees())
        .establishmentYear(infoDto.getEstablishmentYear())
        .build();
  }
}
