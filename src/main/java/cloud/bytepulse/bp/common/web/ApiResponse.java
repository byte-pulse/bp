package cloud.bytepulse.bp.common.web;


import cloud.bytepulse.bp.common.util.json.JsonUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import java.io.IOException;

import static cloud.bytepulse.bp.common.constant.HttpStatusReasonPhraseCN.getReasonPhrase;

/**
 * 操作消息提醒
 *
 * @author jiejiebiezheyang
 * @since 2024-02-02 19:00
 */
public class ApiResponse<T> {

    /**
     * 状态码
     */
    public Integer code;

    /**
     * 返回信息
     */
    public String message;

    /**
     * 数据对象
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public T data;

    /**
     * 内部错误信息
     *
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String e;

    /**
     * 追踪ID
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String traceId;

    /**
     * 时间戳
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Long timestamp;

    /**
     * 初始化一个新创建的 AjaxResult 对象，使其表示一个空消息。
     */
    public ApiResponse() {
    }

    /**
     * 初始化一个新创建的 AjaxResult 对象
     *
     * @param code 状态码
     * @param msg  返回内容
     */
    public ApiResponse(int code, String msg) {
        this.code = code;
        this.message = msg;
    }

    /**
     * 初始化一个新创建的 AjaxResult 对象
     *
     * @param code 状态码
     * @param msg  返回内容
     * @param data 数据对象
     */
    public ApiResponse(int code, String msg, T data) {
        this(code, msg);
        this.data = data;
    }

    /**
     * 自定义消息成功
     */
    public static ApiResponse<Void> success(String message) {
        return new ApiResponse<>(HttpStatus.OK.value(), message);
    }

    /**
     * 无数据成功
     */
    public static ApiResponse<Void> success() {
        return success(getReasonPhrase(HttpStatus.OK)
        );
    }

    /**
     * 有数据成功
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(HttpStatus.OK.value(), getReasonPhrase(HttpStatus.OK), data);
    }

    /**
     * 参数校验失败、JSON 格式错误 , 业务逻辑错误
     */
    public static <T> ApiResponse<T> badRequest(String message, T data) {
        return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), message, data);
    }

    /**
     * 参数校验失败、JSON 格式错误 , 业务逻辑错误
     */
    public static ApiResponse<Void> badRequest(String message) {
        return badRequest(message, null);
    }

    /**
     * 服务器内部错误
     */
    public static ApiResponse<Void> error(String message) {
        return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
    }

    /**
     * 服务器内部错误
     */
    public static ApiResponse<Void> error() {
        return error(getReasonPhrase(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    /**
     * 未登录
     */
    public static ApiResponse<Void> unauthorized(String message) {
        return new ApiResponse<>(HttpStatus.UNAUTHORIZED.value(), message);
    }

    /**
     * 未登录
     */
    public static ApiResponse<Void> unauthorized() {
        return unauthorized(getReasonPhrase(HttpStatus.UNAUTHORIZED));
    }

    /**
     * 无权限
     */
    public static ApiResponse<Void> forbidden() {
        return new ApiResponse<>(HttpStatus.FORBIDDEN.value(), getReasonPhrase(HttpStatus.FORBIDDEN));
    }

    /**
     * 404
     */
    public static ApiResponse<Void> notFound() {
        return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), getReasonPhrase(HttpStatus.NOT_FOUND));
    }

    /**
     * 401 输出
     */
    public static void printUnauthorized(HttpServletResponse response, String msg) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(JsonUtils.toJsonStr(ApiResponse.unauthorized(msg)));
    }

    /**
     * 404 输出
     *
     */
    public static void printNotFound(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        response.setContentType("application/json;charset=UTF-8");
        ApiResponse<Void> notFound = ApiResponse.notFound();
        notFound.message = "资源不存在:" + request.getRequestURI();
        response.getWriter().println(JsonUtils.toJsonStr(notFound));
    }
}