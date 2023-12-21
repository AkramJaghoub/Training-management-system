package ju.example.training_management_system.exception;

public class UnauthorizedStudentAccessException extends RuntimeException {

    public UnauthorizedStudentAccessException(String message) {
        super(message);
    }
}
