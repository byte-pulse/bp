package cloud.bytepulse.bp.common.enums.errorcode;

/**
 * 登陆模块错误枚举
 *
 * @author jiejiebiezheyang
 * @since 2026-03-31 18:55
 */
public enum LoginErrorCode implements ErrorCode {

    CAPTCHA_ERROR(10000, "验证码错误");


    private final int code;

    private final String message;

    LoginErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }
}
