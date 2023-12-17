package ju.example.training_management_system.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class CompanyInfoDto {

    private String email;

    private String companyName;

    private String industry;

    private String location;

    private String phoneNumber;

    private Integer numOfEmployees;

    private Integer establishmentYear;

    private MultipartFile companyImage;

}