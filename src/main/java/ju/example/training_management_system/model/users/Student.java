package ju.example.training_management_system.model.users;

import jakarta.persistence.Entity;
import lombok.*;

import java.util.Map;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Student extends User {

    String firstName;
    String lastName;
    String university;
    String major;

    public Student(String email, String password){
        super(email, password);
    }

    public Student build(Map<String, Object> properties){
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