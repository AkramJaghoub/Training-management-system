package ju.example.training_management_system.exception;

public class PasswordNotMatchException extends RuntimeException {

  public PasswordNotMatchException() {
    super("Password does not match try again");
  }
}
