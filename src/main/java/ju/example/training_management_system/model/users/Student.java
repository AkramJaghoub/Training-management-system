package ju.example.training_management_system.model.users;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Student extends User {

    private String firstName;
    private String lastName;
    private String university;
    private String major;

    @Column(columnDefinition = "LONGBLOB")
    private byte[] image;

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
