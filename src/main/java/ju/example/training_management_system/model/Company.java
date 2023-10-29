package ju.example.training_management_system.model;

import jakarta.persistence.Entity;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.JoinColumn;
import lombok.NoArgsConstructor;

public class Company extends User {
    public Company(String email, String password){
        super(email, password);
    }

    @JoinColumn(name = "user_id")
    private Integer userId;
}
