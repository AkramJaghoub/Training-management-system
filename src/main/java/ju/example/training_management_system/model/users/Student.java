package ju.example.training_management_system.model.users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

import static ju.example.training_management_system.util.Utils.capitalizeFirstLetter;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Student extends User {

    private String firstName;
    private String lastName;
    private String university;
    private String major;
    private Integer graduationYear;

    public Student(String email, String password) {
        super(email, password);
    }

    public Student build(Map<String, Object> properties) {
        Student student = new Student();
        student.setFirstName(capitalizeFirstLetter((String) properties.get("firstName")));
        student.setLastName(capitalizeFirstLetter((String) properties.get("lastName")));
        student.setEmail((String) properties.get("email"));
        student.setPassword((String) properties.get("password"));
        student.setJoinDate(LocalDateTime.now());
        return student;
    }
}
