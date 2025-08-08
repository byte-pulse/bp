package cloud.bytepulse.common.core.util;

import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 生成工具类
 *
 * @author jiejiebiezheyang
 * @since 2024-03-07 14:00
 */
public class GenerateUtils {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /**
     * 生成指定长度id
     */
    public static String generateRandomNumber(int length) {
        // 使用StringBuilder拼接随机数字
        StringBuilder builder = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            // 生成0到9之间的随机数字
            int digit = random.nextInt(10);
            builder.append(digit);
        }
        // 将StringBuilder转换为long类型
        return builder.toString();
    }
}
