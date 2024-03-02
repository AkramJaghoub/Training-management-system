package ju.example.training_management_system.entity;

import static ju.example.training_management_system.model.PostStatus.PENDING;
import static ju.example.training_management_system.util.Utils.capitalizeFirstLetter;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import ju.example.training_management_system.entity.users.CompanyEntity;
import ju.example.training_management_system.model.Advertisement;
import ju.example.training_management_system.model.JobType;
import ju.example.training_management_system.model.PostStatus;
import ju.example.training_management_system.model.WorkMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ADVERTISEMENT")
public class AdvertisementEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String jobTitle;

  private int internsRequired;

  private int jobDuration;

  private String country;

  private String city;

  @CreatedDate private LocalDateTime postDate;

  @Enumerated(EnumType.STRING)
  private PostStatus postStatus;

  @Enumerated(EnumType.STRING)
  private JobType jobType;

  @Enumerated(EnumType.STRING)
  private WorkMode workMode;

  private String applicationLink;

  @Column(columnDefinition = "TEXT")
  private String imageUrl;

  @Column(columnDefinition = "LONGTEXT")
  private String description;

  @ManyToOne private CompanyEntity company;

  public AdvertisementEntity toEntity(Advertisement advertisement) {
    return AdvertisementEntity.builder()
        .jobTitle(capitalizeFirstLetter(advertisement.getJobTitle()))
        .internsRequired(advertisement.getInternsRequired())
        .jobDuration(advertisement.getJobDuration())
        .country(advertisement.getCountry())
        .city(advertisement.getCity())
        .jobType(advertisement.getJobType())
        .workMode(advertisement.getWorkMode())
        .description(advertisement.getDescription())
        .postStatus(PENDING)
        .applicationLink(advertisement.getApplicationLink())
        .build();
  }
}
