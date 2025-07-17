package cloud.bytepulse.common.core.enums;

import lombok.Getter;

/**
 * @author jiejiebiezheyang
 * @since 2026-02-26 16:19
 */
@Getter
public enum OperateEnum {
    LOGIN("LOGIN", "用户登录"),
    LOGOUT("LOGOUT", "用户退出"),
    QUERY("QUERY", "查询"),
    ADD("ADD", "新增"),
    UPDATE("UPDATE", "更新"),
    DELETE("DELETE", "删除"),
    EXPORT("EXPORT", "文件导出"),
    UPLOAD("UPLOAD", "文件上传");

    private final String name;
    private final String desc;

    OperateEnum(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }
}
