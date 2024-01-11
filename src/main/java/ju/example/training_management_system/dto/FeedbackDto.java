package ju.example.training_management_system.dto;

import lombok.Data;

@Data
public class FeedbackDto {
    private long studentId;
    private String companyName;
    private String comment;
    private double rating;
}
