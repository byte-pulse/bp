package cloud.bytepulse.bp.framwork.exception;

/**
 * 自定义异常
 *
 * @author jiejiebiezheyang
 * @since 2024-07-06 14:00
 */
public class BytePulseException extends RuntimeException {
    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public BytePulseException(String message) {
        super(message);
    }
}
