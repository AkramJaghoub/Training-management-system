package ju.example.training_management_system.model;

import lombok.Data;

@Data
public class Feedback {
  private long studentId;
  private String companyName;
  private String comment;
  private double rating;
}
