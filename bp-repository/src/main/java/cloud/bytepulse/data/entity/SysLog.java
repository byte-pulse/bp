package cloud.bytepulse.data.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


import java.util.Date;

/**
 * 系统接口日志 <p> sys_log
 *
 * @author jiejiebiezheyang
 * @since 2026-09-08 17:29:49
 */
@Data
@TableName(value = "sys_log")
public class SysLog {

    /**
     * 日志id
     */
    private Long id;
    /**
     * 追踪id
     */
    private String traceId;
    /**
     * 接口uri
     */
    private String uri;
    /**
     * http请求方式
     */
    private String httpMethod;
    /**
     * 查询参数
     */
    private String queryParams;
    /**
     * 请求体参数
     */
    private String bodyParams;
    /**
     * 响应结果
     */
    private String responseResult;
    /**
     * 请求时间戳
     */
    private Date requestTime;
    /**
     * 请求ip
     */
    private String requestIp;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 请求耗时
     */
    private Long cost;
    /**
     * 异常信息
     */
    private String exception;

}
