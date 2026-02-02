package cloud.bytepulse.bp.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 系统日志
 *
 * @author jiejiebiezheyang
 * @since 2025-04-29 13:00
 */
@Data
@TableName(value = "sys_log")
public class SysLog {
    /**
     * 日志id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 日志id
     */
    @TableField(value = "trace_id")
    private String traceId;

    /**
     * 接口uri
     */
    @TableField(value = "uri")
    private String uri;

    /**
     * http请求方式
     */
    @TableField(value = "http_method")
    private String httpMethod;

    /**
     * 查询参数
     */
    @TableField(value = "query_params")
    private String queryParams;

    /**
     * 请求体参数
     */
    @TableField(value = "body_params")
    private String bodyParams;

    /**
     * 响应结果
     */
    @TableField(value = "response_result")
    private String responseResult;

    /**
     * 请求时间戳
     */
    @TableField(value = "request_time")
    private Date requestTime;

    /**
     * 请求ip
     */
    @TableField(value = "request_ip")
    private String requestIp;

    /**
     * 用户id
     */
    @TableField(value = "user_id")
    private Long userId;

    /**
     * 请求耗时
     */
    @TableField(value = "cost")
    private Long cost;

    /**
     * 请求耗时
     */
    @TableField(value = "exception")
    private String exception;
}