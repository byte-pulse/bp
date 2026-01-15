package cloud.bytepulse.bp.framework.exception;

/**
 * json 解析异常
 *
 * @author jiejiebiezheyang
 * @since 2024-07-06 14:00
 */
public class JsonParseException extends RuntimeException {
    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public JsonParseException(String message) {
        super(message);
    }

    public JsonParseException(Throwable cause) {
        super(cause);
    }
}
