package cloud.bytepulse.common.core.exception.enums;

import cloud.bytepulse.common.core.exception.enums.interfaces.ErrorCode;

/**
 * 登陆模块错误枚举
 *
 * @author jiejiebiezheyang
 * @since 2026-03-31 18:55
 */
public enum AuthErrorCode implements ErrorCode {
    CAPTCHA_ERROR(10001, "验证码错误"),
    LOGIN_FAIL(10002, "登陆失败"),
    TOKEN_INVALID(10003, "token 非法"),
    TOKEN_EXPIRED(10004, "token 过期");

    private final int code;

    private final String message;

    AuthErrorCode(int code, String message) {
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
