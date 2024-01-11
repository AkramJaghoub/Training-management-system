package ju.example.training_management_system.model;

import jakarta.persistence.*;
import ju.example.training_management_system.model.users.Company;
import ju.example.training_management_system.model.users.Student;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Student student;
    @ManyToOne
    private Company company;
    @Column(columnDefinition = "LONGTEXT")
    private String comment;
    private double rating;
}
