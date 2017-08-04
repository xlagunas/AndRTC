package cat.xlagunas.andrtc.data.exception;

/**
 * Created by xlagunas on 17/3/16.
 */
public class InvalidTokenException extends Exception {
    public InvalidTokenException() {
        super();
    }

    public InvalidTokenException(final String message) {
        super(message);
    }

    public InvalidTokenException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InvalidTokenException(final Throwable cause) {
        super(cause);
    }

}
