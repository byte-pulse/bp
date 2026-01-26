package cloud.bytepulse.bp.common.util;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Properties;
import java.util.Random;

/**
 * @author jiejiebiezheyang
 * @since 2025-04-23 16:00
 */
public class CaptchaUtils {

    private static final DefaultKaptcha producer;

    static {
        Properties properties = new Properties();
        properties.put("kaptcha.border", "no");
        properties.put("kaptcha.textproducer.char.string", "0123456789"); // 纯数字
        properties.put("kaptcha.textproducer.char.length", "4"); // 验证码长度
        properties.put("kaptcha.image.width", "160");
        properties.put("kaptcha.image.height", "60");
        properties.put("kaptcha.textproducer.font.size", "40");
        properties.put("kaptcha.textproducer.font.names", "Arial");
        properties.put("kaptcha.noise.impl", "com.google.code.kaptcha.impl.NoNoise"); // 无干扰线

        Config config = new Config(properties);
        producer = new DefaultKaptcha();
        producer.setConfig(config);
    }


    /**
     * 生成Base64编码的验证码图片
     */
    public static String captchaBase64() throws IOException {
        String text = producer.createText();
        BufferedImage image = producer.createImage(text);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", outputStream);
        return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    /**
     * 生成Base64编码的数学计算验证码图片
     *
     * @return 第一个元素是图片的Base64编码，第二个元素是计算结果
     */
    public static String[] mathCaptchaBase64() throws IOException {
        Random random = new Random();
        // 生成简单的数学计算题 (1-10的加减乘)
        int num1 = random.nextInt(10) + 1;
        int num2 = random.nextInt(10) + 1;
        String operator;
        int result;

        switch (random.nextInt(3)) {
            case 0:
                operator = "+";
                result = num1 + num2;
                break;
            case 1:
                operator = "-";
                // 确保结果为正数
                if (num1 < num2) {
                    int temp = num1;
                    num1 = num2;
                    num2 = temp;
                }
                result = num1 - num2;
                break;
            case 2:
                operator = "×";
                result = num1 * num2;
                break;
            default:
                operator = "+";
                result = num1 + num2;
        }

        String text = num1 + " " + operator + " " + num2 + " = ?";

        // 生成验证码图片
        BufferedImage image = producer.createImage(text);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", outputStream);

        String base64Image = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(outputStream.toByteArray());

        return new String[]{base64Image, String.valueOf(result)};
    }
}
