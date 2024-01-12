package ju.example.training_management_system.model.company.advertisement;

import jakarta.persistence.*;
import ju.example.training_management_system.dto.AdvertisementDto;
import ju.example.training_management_system.model.PostStatus;
import ju.example.training_management_system.model.users.Company;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static ju.example.training_management_system.model.PostStatus.PENDING;
import static ju.example.training_management_system.util.Utils.capitalizeFirstLetter;

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

    private String country;

    private String city;

    private LocalDateTime postDate;

    @Enumerated(EnumType.STRING)
    private PostStatus postStatus;

    @Enumerated(EnumType.STRING)
    private JobType jobType;

    @Enumerated(EnumType.STRING)
    private WorkMode workMode;

    private String applicationLink;

    @Column(columnDefinition = "LONGBLOB")
    private byte[] image;

    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @ManyToOne
    private Company company;

    public Advertisement toEntity(AdvertisementDto adDto) {
        return Advertisement.builder()
                .jobTitle(capitalizeFirstLetter(adDto.getJobTitle()))
                .internsRequired(adDto.getInternsRequired())
                .jobDuration(adDto.getJobDuration())
                .country(adDto.getCountry())
                .city(adDto.getCity())
                .jobType(adDto.getJobType())
                .workMode(adDto.getWorkMode())
                .description(adDto.getDescription())
                .postDate(LocalDateTime.now())
                .postStatus(PENDING)
                .applicationLink(adDto.getApplicationLink())
                .build();
    }
}
