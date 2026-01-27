package cloud.bytepulse.bp.domain.mapper;

import cloud.bytepulse.bp.domain.entity.ApiCredentials;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @author jiejiebiezheyang
 * @since 2026-01-20 12:17:26
 */
public interface ApiCredentialsMapper extends BaseMapper<ApiCredentials> {
    ApiCredentials selectByApiKeyHash(String apiKeyHash);
}




