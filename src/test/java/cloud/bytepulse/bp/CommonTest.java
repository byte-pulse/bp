package cloud.bytepulse.bp;

import com.fasterxml.jackson.databind.JsonNode;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author jiejiebiezheyang
 * @since 2025-12-09 16:10
 */
@SpringBootTest
public class CommonTest {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

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
