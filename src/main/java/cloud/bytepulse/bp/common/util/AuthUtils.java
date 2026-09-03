package cloud.bytepulse.bp.common.util;

import cloud.bytepulse.bp.domain.models.auth.LoginUser;
import cloud.bytepulse.bp.domain.models.auth.LoginUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 登录工具类
 *
 * @author jiejiebiezheyang
 * @since 2025-04-29 13:00
 */
@Component
@RequiredArgsConstructor
public class AuthUtils {

    /**
     * 获取当前登录用户
     */
    public static LoginUser getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        // 未登录状态下
        if ("anonymousUser".equals(authentication.getPrincipal())) {
            LoginUser loginUser = new LoginUser();
            LoginUserInfo loginUserInfo = new LoginUserInfo();
            loginUserInfo.setUserId(0L);
            loginUserInfo.setUsername("anonymousUser");
            loginUserInfo.setNickname("anonymousUser");
            loginUser.setLoginUserInfo(loginUserInfo);
            return loginUser;
        }
        return (LoginUser) authentication.getPrincipal();
    }

    /**
     * 获取当前用户信息
     */
    public static LoginUserInfo getUser() {
        LoginUser loginUser = getLoginUser();
        if (loginUser == null) {
            return null;
        }
        return loginUser.getLoginUserInfo();
    }

    /**
     * 获取当前用户id
     */
    public static Long getUserId() {
        LoginUserInfo user = getUser();
        if (user == null) {
            return 0L;
        }
        return user.getUserId();
    }

    /**
     * 获取当前用户操作权限
     */
    public static Set<String> getPermissions() {
        LoginUser loginUser = getLoginUser();
        if (loginUser == null) {
            return new HashSet<>();
        }
        return loginUser.getPermissions();
    }
}
