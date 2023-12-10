package ju.example.training_management_system.dto;

import ju.example.training_management_system.model.company.advertisement.JobType;
import ju.example.training_management_system.model.company.advertisement.WorkMode;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AdvertisementDto {

    private String jobTitle;

    private int internsRequired;

    private int jobDuration;

    private String country;

    private String city;

    private JobType jobType;

    private WorkMode workMode;

    private MultipartFile jobImage;

    private String description;
}
