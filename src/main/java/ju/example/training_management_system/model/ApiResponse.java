package ju.example.training_management_system.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class ApiResponse {
  private String message;
  private HttpStatus status;
}
