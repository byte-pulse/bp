package cloud.bytepulse.bp.domain.models.auth.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author jiejiebiezheyang
 * @since 2024-03-03 11:00
 */
@Data
@NoArgsConstructor
public class LoginUser implements UserDetails {

    public static final Hashtable<Integer, Integer> NEED_RE_LOGIN;

    static {
        NEED_RE_LOGIN = new Hashtable<>();
    }

    /**
     * 用户信息
     */
    private LoginUserInfo loginUserInfo;

    /**
     * 用户信息
     */
    @JsonIgnore
    private String password;

    /**
     * 操作权限列表
     */
    private Set<String> permissions;

    public LoginUser(LoginUserInfo loginUserInfo, String password, Set<String> permissions) {
        this.loginUserInfo = loginUserInfo;
        this.password = password;
        this.permissions = permissions;
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 把permissions中的权限信息封装成SimpleGrantedAuthority对象
        return permissions.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        if (loginUserInfo == null) {
            return null;
        }
        return loginUserInfo.getUsername();
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }
}
