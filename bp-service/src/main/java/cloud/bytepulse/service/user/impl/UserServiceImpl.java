package cloud.bytepulse.service.user.impl;

import cloud.bytepulse.data.entity.SysUser;
import cloud.bytepulse.data.mapper.SysUserMapper;
import cloud.bytepulse.security.Auths;
import cloud.bytepulse.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author jiejiebiezheyang
 * @since 2026-11-09 16:17
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final SysUserMapper sysUserMapper;

    /**
     * 查询当前登录用户的信息
     */
    @Override
    public SysUser getCurrentUserStatus() {
        return sysUserMapper.selectCurrentUserStatus(Auths.getUserId());
    }

    /**
     * 查询用户信息
     *
     * @param userId 用户id
     */
    @Override
    public SysUser getUserStatus(Long userId) {
        return sysUserMapper.selectUserStatus(userId);
    }
}
