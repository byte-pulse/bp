package cloud.bytepulse.bp.framwork.enums;

/**
 * 响应码
 *
 * @author jiejiebiezheyang
 * @since 2025-04-24 12:00
 */
public enum HttpStatusEnum {

    /**
     * 请求成功，返回响应数据
     */
    OK(200, "成功"),

    /**
     * 参数校验失败、JSON 格式错误
     */
    BAD_REQUEST(400, "参数校验失败"),

    /**
     * 未登录或 Token 失效, 登陆时用户名密码错误
     */
    UNAUTHORIZED(401, "未登录"),

    /**
     * 有 Token 但权限不足
     */
    FORBIDDEN(403, "无权限"),

    /**
     * 请求的 URL 或 ID 不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 如用 POST 访问只支持 GET 的接口
     */
    METHOD_NOT_ALLOWED(405, "方法不允许"),

    /**
     * 服务器内部错误
     */
    INTERNAL_SERVER_ERROR(500, "服务器内部错误");

    /**
     * 状态码
     */
    public final int code;

    /**
     * 默认提示消息
     */
    public final String message;

    HttpStatusEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
