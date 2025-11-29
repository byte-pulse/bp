package cloud.bytepulse.data.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


import java.util.Date;

/**
 * 系统用户 <p> sys_user
 *
 * @author jiejiebiezheyang
 * @since 2026-09-08 17:29:49
 */
@Data
@TableName(value = "sys_user")
public class SysUser {

    /**
     * 管理员id
     */
    private Long id;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码(加密)
     */
    private String password;
    /**
     * 上一次登录时间
     */
    private Date lastLogin;
    /**
     * 上一次登录IP
}
