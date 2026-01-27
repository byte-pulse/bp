package cloud.bytepulse.bp.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 接口凭据
 *
 * @author jiejiebiezheyang
 * @since 2025-08-15 12:00
 */
@Data
@TableName(value = "api_credentials")
public class ApiCredentials {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
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