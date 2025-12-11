package cloud.bytepulse.bp.common.utils;

import java.util.UUID;

/**
 *
 * @author jiejiebiezheyang
 * @since 2023-04-03 14:05
 */
public class TraceIdUtil {

    private static final ThreadLocal<String> TRACE_ID = new ThreadLocal<>();

    public static String init() {
        String id = UUID.randomUUID().toString().replace("-", "");
        TRACE_ID.set(id);
        return id;
    }

    public static String get() {
        return TRACE_ID.get();
    }

    public static void clear() {
        TRACE_ID.remove();
    }
}
