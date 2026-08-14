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

    /**
     * 上一次登IP
     */
    private String lastLoginIp;

    /**
     * 最后更新日期
     */
    private Date lastUpdate;

    /**
     * 登录会话 指纹
     */
    private String fingerprint;

    public LoginUserInfo() {
    }

    public LoginUserInfo(SysUser sysUser) {
        this.userId = sysUser.getId();
        this.nickname = sysUser.getNickname();
        this.username = sysUser.getUsername();
        this.lastLogin = sysUser.getLastLogin();
        this.lastLoginIp = sysUser.getLastLoginIp();
        this.lastUpdate = sysUser.getLastUpdate();
    }
}
