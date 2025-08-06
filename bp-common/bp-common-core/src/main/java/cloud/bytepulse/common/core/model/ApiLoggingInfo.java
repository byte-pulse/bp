package cloud.bytepulse.common.core.model;

import cloud.bytepulse.common.core.enums.OperateEnum;
import lombok.Data;

/**
 * @author jiejiebiezheyang
 * @since 2026-09-10 15:03
 */
@Data
public class ApiLoggingInfo {

    /**
     * 路径
     */
    private String path;

    /**
     * 操作类型
     */
    private OperateEnum operate;

    /**
     * 描述
     */
    private String desc;

    public ApiLoggingInfo() {
        this.path = "";
        this.operate = OperateEnum.QUERY;
        this.desc = "";
    }

    public ApiLoggingInfo(String path, OperateEnum operate, String desc) {
        this.path = path;
        this.operate = operate;
        this.desc = desc;
    }
}
