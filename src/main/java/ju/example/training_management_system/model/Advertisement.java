package ju.example.training_management_system.model;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class Advertisement {

  private long id;

  private String jobTitle;

  private int internsRequired;

  private int jobDuration;

  private String country;

  private String city;

  private JobType jobType;

  private WorkMode workMode;

  private MultipartFile jobImage;

  private String applicationLink;

  private String description;
}
