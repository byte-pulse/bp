package cloud.bytepulse.security.impl;


import cloud.bytepulse.data.entity.SysUser;
import cloud.bytepulse.data.mapper.SysUserMapper;
import cloud.bytepulse.security.LoginUser;
import cloud.bytepulse.security.LoginUserInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
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
}
