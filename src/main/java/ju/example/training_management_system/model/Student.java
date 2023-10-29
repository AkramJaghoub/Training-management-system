package ju.example.training_management_system.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import lombok.NoArgsConstructor;

public class Student extends User {
    public Student(String email, String password){
        super(email, password);
    }

    @JoinColumn(name = "user_id")
    private Integer userId;
}