package cloud.bytepulse.bp.domain.models.auth.pojo;

import cloud.bytepulse.bp.domain.models.entity.SysUser;
import lombok.Data;

import java.util.Date;

/**
 * 登录用户信息
 *
 * @author jiejiebiezheyang
 * @since 2025-04-29 16:00
 */
@Data
public class LoginUserInfo {

    /**
     * 用户id
     */
    private Integer userId;

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
     * 登录会话id
     */
    private String sessionId;

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
