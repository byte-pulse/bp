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
     * 操作类型
     */
    private String operate;
    /**
     * 操作描述
     */
    private String description;
    /**
     * 接口uri
     */
    private String uri;
    /**
     * http请求方式
     */
    private String httpMethod;
}
