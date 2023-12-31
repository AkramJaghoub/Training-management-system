package ju.example.training_management_system.exception;

public class NotificationDoesNotExistException extends RuntimeException{

    public NotificationDoesNotExistException(String message){
        super(message);
    }
}
