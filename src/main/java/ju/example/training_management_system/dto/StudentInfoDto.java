package ju.example.training_management_system.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class StudentInfoDto {
    private String email;
    private String firstName;
    private String lastName;
    private String university;
    private String major;
    private String phoneNumber;
    private String address;
    private Integer graduationYear;
    private MultipartFile studentImage;
}
