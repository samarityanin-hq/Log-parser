package academy.incidentradar.validation;

public class UsageException extends RuntimeException {
    public UsageException(String message) {
        super(message);
    }

    public UsageException(String message, Throwable e) {
        super(message, e);
    }
}
