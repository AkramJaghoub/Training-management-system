package ju.example.training_management_system.model.company.advertisement;

import jakarta.persistence.*;
import ju.example.training_management_system.dto.AdvertisementDto;
import ju.example.training_management_system.model.users.Company;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String jobTitle;

    private int internsRequired;

    private int jobDuration;

    @Enumerated(EnumType.STRING)
    private JobType jobType;

    @Enumerated(EnumType.STRING)
    private WorkMode workMode;

    private String description;

    @ManyToOne
    private Company company;

    public Advertisement toEntity(AdvertisementDto postDto) {
        return Advertisement.builder()
                .jobTitle(postDto.getJobTitle())
                .internsRequired(postDto.getInternsRequired())
                .jobDuration(postDto.getJobDuration())
                .jobType(postDto.getJobType())
                .workMode(postDto.getWorkMode())
                .description(postDto.getDescription())
                .build();
    }
}