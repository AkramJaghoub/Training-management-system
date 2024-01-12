package ju.example.training_management_system.exception;

public class FeedbackDoesNotExistException extends RuntimeException{

    public FeedbackDoesNotExistException(String message){
        super(message);
    }
}
