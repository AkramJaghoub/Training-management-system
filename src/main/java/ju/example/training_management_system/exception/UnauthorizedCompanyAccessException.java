package ju.example.training_management_system.exception;

public class UnauthorizedCompanyAccessException extends RuntimeException {

    public UnauthorizedCompanyAccessException(String message){
        super(message);
    }
}
