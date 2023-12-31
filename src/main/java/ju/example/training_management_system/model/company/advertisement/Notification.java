package ju.example.training_management_system.model.company.advertisement;

import jakarta.persistence.*;
import ju.example.training_management_system.model.users.Company;
import lombok.Data;

@Data
@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String message;
    @ManyToOne
    private Company company;
}
