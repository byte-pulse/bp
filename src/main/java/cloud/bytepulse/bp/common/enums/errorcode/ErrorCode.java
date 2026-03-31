package cloud.bytepulse.bp.common.enums.errorcode;

/**
 * @author jiejiebiezheyang
 * @since 2026-03-31 19:04
 */
public interface ErrorCode {

    /**
     * 获取错误码
     */
    int code();

    /**
     * 获取错误信息
     */
    String message();
}
