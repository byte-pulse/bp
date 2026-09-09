package cloud.bytepulse.data.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


import java.util.Date;

/**
 * 接口凭证 <p> api_credentials
 *
 * @author jiejiebiezheyang
 * @since 2026-09-08 17:29:48
 */
@Data
@TableName(value = "api_credentials")
public class ApiCredentials {

    /**
     *
     */
    private Long id;
    /**
     * 第三方用户或应用 ID
     */
    private Long ownerId;
    /**
     * SHA-256(apiKey)
     */
    private String apiKeyHash;
    /**
     * 加密后的 apiSecret
     */
    private String apiSecretEnc;
    /**
     * ACTIVE DISABLED REVOKED
     */
    private String status;
    /**
     * 权限范围
     */
    private String scope;
    /**
     * 套餐类型
     */
    private String plan;
    /**
     * 过期时间
     */
    private Date expiresAt;
    /**
     * 最后一次调用
     */
    private Date lastUsedAt;
    /**
     *
     */
    private Date createdAt;
    /**
     *
     */
    private Date updatedAt;

}
