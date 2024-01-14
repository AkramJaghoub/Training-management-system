package ju.example.training_management_system.exception;

public class PasswordReusedException extends RuntimeException {
  public PasswordReusedException(String message) {
    super(message);
  }
}
