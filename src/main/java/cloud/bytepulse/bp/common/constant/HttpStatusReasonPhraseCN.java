package cloud.bytepulse.bp.common.constant;

import org.springframework.http.HttpStatus;

/**
 * 响应码
 *
 * @author jiejiebiezheyang
 * @since 2025-04-24 12:00
 */
public class HttpStatusReasonPhraseCN {

    /**
     * 返回 httpStatus 中文简短原因
     *
     * @param httpStatus http 状态码
     */
    public static String getReasonPhrase(HttpStatus httpStatus) {
        return switch (httpStatus) {
            case OK -> "成功";
            case BAD_REQUEST -> "错误的请求";
            case UNAUTHORIZED -> "未认证";
            case FORBIDDEN -> "无权限";
            case NOT_FOUND -> "资源不存在";
            case METHOD_NOT_ALLOWED -> "方法不允许";
            case INTERNAL_SERVER_ERROR -> "服务器内部错误";
            default -> "未知错误";
        };
    }
}
