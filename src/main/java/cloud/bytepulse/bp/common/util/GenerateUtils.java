package cloud.bytepulse.bp.common.util;

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


    /**
     * 获得指定长度的随机字符串
     */
    public static String generate(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive");
        }
        StringBuilder sb = new StringBuilder(length);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARS.length());
            sb.append(CHARS.charAt(index));
        }
        return sb.toString();
    }


    /**
     * 当天yyyyMMdd0000001格式id生成
     */
    public static String generateId(String currentId) {
        String prefix = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String suffix = "0000001";
        if (StringUtils.hasText(currentId)) {
            String number = currentId.substring(8);
            int i = Integer.parseInt(number) + 1;
            // 字符串格式化补零
            suffix = String.format("%07d", i);
        }
        return prefix + suffix;
    }

    /**
     * 将字节数转换为可读格式
     */
    public static String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.2f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", bytes / (1024.0 * 1024));
        } else {
            return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
        }
    }
}
