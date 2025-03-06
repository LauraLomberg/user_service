package school.faang.user_service.exception;

public class ValidationException extends UserServiceException {
    public ValidationException(ErrorCode message) {
        super(message);
    }
}
