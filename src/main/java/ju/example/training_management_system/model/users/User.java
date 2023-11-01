package ju.example.training_management_system.model.users;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    protected String email;

    protected String password;

    @Enumerated(EnumType.STRING)
    protected Role role;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}