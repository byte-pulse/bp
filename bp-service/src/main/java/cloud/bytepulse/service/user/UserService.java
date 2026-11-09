package cloud.bytepulse.service.user;

import cloud.bytepulse.data.entity.SysUser;

/**
 * @author jiejiebiezheyang
 * @since 2026-11-09 16:17
 */
public interface UserService {

    /**
     * 查询当前登录用户的信息
     */
    SysUser getCurrentUserStatus();

    /**
     * 查询用户信息
     */
    SysUser getUserStatus(Long userId);
}
