package cloud.bytepulse.bp;

import cloud.bytepulse.bp.common.util.minio.properties.IMinioProperties;
import cloud.bytepulse.bp.domain.mapper.FileMetadataMapper;
import com.fasterxml.jackson.databind.JsonNode;
import io.minio.MinioClient;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author jiejiebiezheyang
 * @since 2025-12-09 16:10
 */
@Slf4j
@SpringBootTest
public class CommonTest {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private FileMetadataMapper fileMetadataMapper;
    @Autowired
    private MinioClient minioClient;
    @Autowired
    private IMinioProperties iMinioProperties;


    @Test
    public void passwordEncoder() {
        String encoded = bCryptPasswordEncoder.encode("000000");
        System.out.println(encoded);
    }

    @Test
    public void test() {
        HttpResponse<JsonNode> json = Unirest.get("https://paper.hengcloud.top/v1/parse?link=4http://xhslink.com/o/3KiWE7rQMNY")
                .asObject(JsonNode.class);
        System.out.println(json.getBody());
    }
}
