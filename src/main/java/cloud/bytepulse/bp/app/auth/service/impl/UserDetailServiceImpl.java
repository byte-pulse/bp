package cloud.bytepulse.bp.app.auth.service.impl;

import cloud.bytepulse.bp.domain.entity.SysUser;
import cloud.bytepulse.bp.domain.mapper.SysUserMapper;
import cloud.bytepulse.bp.domain.models.auth.pojo.LoginUser;
import cloud.bytepulse.bp.domain.models.auth.pojo.LoginUserInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * 用户信息加载
 *
 * @author jiejiebiezheyang
 * @since 2024-03-02 22:00
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final SysUserMapper sysUserMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws RuntimeException {
        username = username.trim();
        // 从数据库获取用户信息
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "1");
        // 判断手机号（11位数字，以'1'开头）
        if (username.matches("^1[3-9]\\d{9}$")) {
            wrapper.eq("phone", username);
        } else if (username.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            wrapper.eq("email", username);
        } else {
            wrapper.eq("username", username);
        }
        SysUser sysUser = sysUserMapper.selectOne(wrapper);
        if (sysUser == null) {
            throw new BadCredentialsException("用户名或密码错误");
        } else {
            // TODO 用户操作权限信息
            Set<String> permissions = new HashSet<>();
            LoginUserInfo loginUserInfo = new LoginUserInfo(sysUser);
            return new LoginUser(loginUserInfo, sysUser.getPassword(), permissions);
        }
    }
}
