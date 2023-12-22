package ju.example.training_management_system.model.manage.student;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import ju.example.training_management_system.dto.CompanyInfoDto;
import ju.example.training_management_system.dto.StudentInfoDto;
import ju.example.training_management_system.model.manage.company.CompanyInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class StudentInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String university;
    private String major;
    private String phoneNumber;
    private String address;
    private Integer graduationYear;

    public StudentInfo toEntity(StudentInfoDto infoDto) {
        System.out.println(infoDto);
        return StudentInfo.builder()
                .email(infoDto.getEmail())
                .firstName(infoDto.getFirstName())
                .lastName(infoDto.getLastName())
                .phoneNumber(infoDto.getPhoneNumber())
                .address(infoDto.getAddress())
                .graduationYear(infoDto.getGraduationYear())
                .university(infoDto.getUniversity())
                .major(infoDto.getMajor())
                .build();
    }
}
