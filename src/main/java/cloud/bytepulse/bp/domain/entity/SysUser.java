package cloud.bytepulse.bp.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 *
 * @author jiejiebiezheyang
 * @since 2025-08-15 12:00
 */
@Data
@TableName(value = "sys_user")
public class SysUser {
    /**
     * 管理员id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 昵称
     */
    @TableField(value = "nickname")
    private String nickname;

    /**
     * 用户名
     */
    @TableField(value = "username")
    private String username;

    /**
     * 密码(加密)
     */
    @TableField(value = "`password`")
    private String password;

    /**
     * 上一次登录时间
     */
    @TableField(value = "last_login")
    private Date lastLogin;

    /**
     * 上一次登IP
     */
    @TableField(value = "last_login_ip")
    private String lastLoginIp;

    /**
     * 最后更新日期
     */
    @TableField(value = "last_update")
    private Date lastUpdate;

    /**
     * 状态 0 禁用 1 启用
     */
    @TableField(value = "`status`")
    private Object status;

    /**
     * 简介
     */
    @TableField(value = "introduction")
    private String introduction;

    /**
     * 头像
     */
    @TableField(value = "avatar")
    private String avatar;

    /**
     * 手机号
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 电子邮箱
     */
    @TableField(value = "email")
    private String email;

    /**
     * 注册日期
     */
    @TableField(value = "register_date")
    private Date registerDate;
}