package cloud.bytepulse.bp;

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
}
