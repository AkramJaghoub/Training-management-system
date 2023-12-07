package ju.example.training_management_system.model.users;

import jakarta.persistence.Entity;
import java.util.Map;
import lombok.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Student extends User {

  private String firstName;
  private String lastName;
  private String university;
  private String major;

  public Student(String email, String password) {
    super(email, password);
  }

  public Student build(Map<String, Object> properties) {
    Student student = new Student();
    student.setFirstName((String) properties.get("firstName"));
    student.setLastName((String) properties.get("lastName"));
    student.setEmail((String) properties.get("email"));
    student.setUniversity((String) properties.get("university"));
    student.setMajor((String) properties.get("major"));
    student.setPassword((String) properties.get("password"));
    return student;
  }
}
