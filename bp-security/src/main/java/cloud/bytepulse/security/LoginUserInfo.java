package cloud.bytepulse.security;

import cloud.bytepulse.data.entity.SysUser;
import lombok.Data;

import java.util.Date;

/**
 * @author jiejiebiezheyang
 * @since 2026-09-08 17:35
 */
@Data
public class LoginUserInfo {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 用户名
     */
    private String username;

    /**
     * 上一次登录时间
     */
    private Date lastLogin;

}
