package ju.example.training_management_system.model;

import ju.example.training_management_system.dto.StudentInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentInfo {
  private String email;
  private String firstName;
  private String lastName;
  private String university;
  private String major;
  private Integer graduationYear;

  public StudentInfo toModel(StudentInfoDto infoDto) {
    return StudentInfo.builder()
        .email(infoDto.getEmail())
        .firstName(infoDto.getFirstName())
        .lastName(infoDto.getLastName())
        .graduationYear(infoDto.getGraduationYear())
        .university(infoDto.getUniversity())
        .major(infoDto.getMajor())
        .build();
  }
}
