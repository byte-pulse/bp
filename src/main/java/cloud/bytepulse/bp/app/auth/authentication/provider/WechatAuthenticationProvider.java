package cloud.bytepulse.bp.app.auth.authentication.provider;

import cloud.bytepulse.bp.app.auth.authentication.token.WechatAuthenticationToken;
import cloud.bytepulse.bp.domain.entity.SysUser;
import cloud.bytepulse.bp.domain.mapper.SysUserMapper;
import cloud.bytepulse.bp.domain.models.auth.LoginUser;
import cloud.bytepulse.bp.domain.models.auth.LoginUserInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 微信认证
 *
 * @author jiejiebiezheyang
 * @since 2026-03-25 18:47
 */
@Component
@RequiredArgsConstructor
public class WechatAuthenticationProvider implements AuthenticationProvider {

    private final SysUserMapper sysUserMapper;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        WechatAuthenticationToken token = (WechatAuthenticationToken) authentication;

        String openid = (String) token.getPrincipal();
        QueryWrapper<SysUser> wrapper = new QueryWrapper<>();
        // TODO 根据openid查询用户信息 这里临时用username代替
        wrapper.eq("username", openid);
        wrapper.eq("status", "1");
        SysUser user = sysUserMapper.selectOne(wrapper);
        if (user == null) {
            throw new BadCredentialsException("用户未注册或被禁用");
        }

        // 保存用户信息
        LoginUserInfo loginUserInfo = new LoginUserInfo(user);
        Set<String> permissions = new HashSet<>();
        LoginUser loginUser = new LoginUser(loginUserInfo, null, permissions);

        return new WechatAuthenticationToken(loginUser, loginUser.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return WechatAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
