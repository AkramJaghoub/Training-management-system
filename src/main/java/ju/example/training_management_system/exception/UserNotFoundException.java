package ju.example.training_management_system.exception;

public class UserNotFoundException extends RuntimeException{
    public UserNotFoundException(){
        super("User couldn't be found");
    }
}
