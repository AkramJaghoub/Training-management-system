package ju.example.training_management_system.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import lombok.NoArgsConstructor;
import jakarta.persistence.DiscriminatorValue;

public class Admin extends User {
    public Admin(String email, String password){
        super(email, password);
    }

    @JoinColumn(name = "user_id")
    private Integer userId;
}