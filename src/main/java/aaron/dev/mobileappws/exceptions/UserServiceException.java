package aaron.dev.mobileappws.exceptions;

public class UserServiceException extends RuntimeException {

    private static final long serialVersionUID = -7016788661636422993L;

    public UserServiceException(String message){
        super(message);
    }
}
