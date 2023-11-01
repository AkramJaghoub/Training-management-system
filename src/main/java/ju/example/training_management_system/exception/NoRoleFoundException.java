package ju.example.training_management_system.exception;

public class NoRoleFoundException extends RuntimeException{

    public NoRoleFoundException(){
        super("no role found");
    }

}
