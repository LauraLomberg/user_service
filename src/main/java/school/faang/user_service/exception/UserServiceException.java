package school.faang.user_service.exception;

public abstract class UserServiceException extends RuntimeException {
    public UserServiceException(ErrorCode errorCode) {
        super(errorCode.getDescription());
    }
}
