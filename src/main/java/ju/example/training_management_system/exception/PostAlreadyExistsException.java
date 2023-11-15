package ju.example.training_management_system.exception;

public class PostAlreadyExistsException extends RuntimeException{

    public PostAlreadyExistsException(String message){
        super(message);
    }
}
