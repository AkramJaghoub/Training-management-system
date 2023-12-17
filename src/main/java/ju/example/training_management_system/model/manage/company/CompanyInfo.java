package ju.example.training_management_system.model.manage.company;

import jakarta.persistence.*;
import ju.example.training_management_system.dto.CompanyInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class CompanyInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String companyName;

    private String industry;

    private String phoneNumber;

    private String location;

    private Integer numOfEmployees;

    private Integer establishmentYear;

    public CompanyInfo toEntity(CompanyInfoDto infoDto) {
        System.out.println(infoDto);
        return CompanyInfo.builder()
                .email(infoDto.getEmail())
                .companyName(infoDto.getCompanyName())
                .industry(infoDto.getIndustry())
                .phoneNumber(infoDto.getPhoneNumber())
                .location(infoDto.getLocation())
                .numOfEmployees(infoDto.getNumOfEmployees())
                .establishmentYear(infoDto.getEstablishmentYear())
                .build();
    }
}
