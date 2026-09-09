package cloud.bytepulse.service.auth.vo;

import lombok.Data;

import java.util.Date;

/**
 * 登录成功信息
 *
 * @author jiejiebiezheyang
 * @since 2025-04-29 17:00
 */
@Data
public class LoginResultVO {

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
     * token
     */
    private String token;

}
