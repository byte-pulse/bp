package cloud.bytepulse.bp.app.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author jiejiebiezheyang
 * @since 2025-04-29 12:00
 */
@Data
public class LoginDTO {

    @NotBlank(message = "用户名为空")
    private String username;

    @NotBlank(message = "密码为空")
    private String password;

    @NotBlank(message = "验证码为空")
    private String captcha;

    @NotBlank(message = "验证码为空")
    private String uid;
}
