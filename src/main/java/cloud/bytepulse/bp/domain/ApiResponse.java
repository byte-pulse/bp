package cloud.bytepulse.bp.domain;


import cloud.bytepulse.bp.framework.enums.HttpStatusEnum;

import java.io.Serial;
import java.util.HashMap;

/**
 * 操作消息提醒
 *
 * @author jiejiebiezheyang
 * @since 2024-02-02 19:00
 */
public class ApiResponse extends HashMap<String, Object> {

    /**
     * 状态码
     */
    public static final String CODE_TAG = "code";

    /**
     * 返回内容
     */
    public static final String MSG_TAG = "message";

    /**
     * 数据对象
     */
    public static final String DATA_TAG = "data";

    /**
     * 请求ID
     */
    public static final String REQUEST_ID = "request_id";

    /**
     * 时间戳
     *
     */
    public static final String TIMESTAMP = "timestamp";

    @Serial
    private static final long serialVersionUID = 1L;

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
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
    }

    /**
     * 初始化一个新创建的 AjaxResult 对象
     *
     * @param code 状态码
     * @param msg  返回内容
     * @param data 数据对象
     */
    public ApiResponse(int code, String msg, Object data) {
        this(code, msg);
        if (data != null) {
            super.put(DATA_TAG, data);
        }
    }

    /**
     * 方便链式调用
     */
    @Override
    public ApiResponse put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    /*
     * 自定义消息成功
     * */
    public static ApiResponse success(String message) {
        return new ApiResponse(HttpStatusEnum.OK.code, message);
    }

    /**
     * 无数据成功
     */
    public static ApiResponse success() {
        return success(HttpStatusEnum.OK.message);
    }

    /**
     * 有数据成功
     */
    public static ApiResponse success(Object data) {
        return success().put(DATA_TAG, data);
    }

    /**
     * 参数校验失败、JSON 格式错误 , 业务逻辑错误
     */
    public static ApiResponse badRequest(String message, Object data) {
        return new ApiResponse(HttpStatusEnum.BAD_REQUEST.code, message, data);
    }

    /**
     * 参数校验失败、JSON 格式错误 , 业务逻辑错误
     */
    public static ApiResponse badRequest(String message) {
        return badRequest(message, null);
    }

    /**
     * 服务器内部错误
     */
    public static ApiResponse error(String message) {
        return new ApiResponse(HttpStatusEnum.INTERNAL_SERVER_ERROR.code, message);
    }

    /**
     * 服务器内部错误
     */
    public static ApiResponse error() {
        return error(HttpStatusEnum.INTERNAL_SERVER_ERROR.message);
    }

    /**
     * 未登录
     */
    public static ApiResponse unauthorized(String message) {
        return new ApiResponse(HttpStatusEnum.UNAUTHORIZED.code, message);
    }

    /*
     * 未登录
     * */
    public static ApiResponse unauthorized() {
        return unauthorized(HttpStatusEnum.UNAUTHORIZED.message);
    }

    /**
     * 无权限
     */
    public static ApiResponse forbidden() {
        return new ApiResponse(HttpStatusEnum.FORBIDDEN.code, HttpStatusEnum.FORBIDDEN.message);
    }

    /**
     * 404
     */
    public static ApiResponse notFound() {
        return new ApiResponse(HttpStatusEnum.NOT_FOUND.code, HttpStatusEnum.NOT_FOUND.message);
    }
}