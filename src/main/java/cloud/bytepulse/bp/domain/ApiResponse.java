package cloud.bytepulse.bp.domain;


import cloud.bytepulse.bp.common.enums.HttpStatusEnum;
import com.fasterxml.jackson.annotation.JsonInclude;

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
        this.code = code;
        this.message = msg;
        this.data = data;
    }

    /**
     * 自定义消息成功
     */
    public static ApiResponse<Void> success(String message) {
        return new ApiResponse<>(HttpStatusEnum.OK.code, message);
    }

    /**
     * 无数据成功
     */
    public static ApiResponse<Void> success() {
        return success(HttpStatusEnum.OK.message);
    }

    /**
     * 有数据成功
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(HttpStatusEnum.OK.code, HttpStatusEnum.OK.message, data);
    }

    /**
     * 参数校验失败、JSON 格式错误 , 业务逻辑错误
     */
    public static <T> ApiResponse<T> badRequest(String message, T data) {
        return new ApiResponse<>(HttpStatusEnum.BAD_REQUEST.code, message, data);
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
        return new ApiResponse<>(HttpStatusEnum.INTERNAL_SERVER_ERROR.code, message);
    }

    /**
     * 服务器内部错误
     */
    public static ApiResponse<Void> error() {
        return error(HttpStatusEnum.INTERNAL_SERVER_ERROR.message);
    }

    /**
     * 未登录
     */
    public static ApiResponse<Void> unauthorized(String message) {
        return new ApiResponse<>(HttpStatusEnum.UNAUTHORIZED.code, message);
    }

    /*
     * 未登录
     * */
    public static ApiResponse<Void> unauthorized() {
        return unauthorized(HttpStatusEnum.UNAUTHORIZED.message);
    }

    /**
     * 无权限
     */
    public static ApiResponse<Void> forbidden() {
        return new ApiResponse<>(HttpStatusEnum.FORBIDDEN.code, HttpStatusEnum.FORBIDDEN.message);
    }

    /**
     * 404
     */
    public static ApiResponse<Void> notFound() {
        return new ApiResponse<>(HttpStatusEnum.NOT_FOUND.code, HttpStatusEnum.NOT_FOUND.message);
    }
}