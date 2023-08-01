package proj.chat.exception;

public class NotValidateEmailException extends RuntimeException {
    
    public NotValidateEmailException(String message) {
        super(message);
    }
}
