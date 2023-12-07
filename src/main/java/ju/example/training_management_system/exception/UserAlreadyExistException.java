package ju.example.training_management_system.exception;

public class UserAlreadyExistException extends RuntimeException {

  public UserAlreadyExistException() {
    super("user already exists");
  }
}
