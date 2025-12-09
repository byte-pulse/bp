package cloud.bytepulse.bp.domain.models.entity;

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
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 接口名称
     */
    @TableField(value = "api_name")
    private String apiName;

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
     * 执行的方法
     */
    @TableField(value = "`method`")
    private String method;

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
    private Integer userId;

    /**
     * 请求耗时
     */
    @TableField(value = "cost")
    private Long cost;
}