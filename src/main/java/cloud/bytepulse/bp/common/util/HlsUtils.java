package cloud.bytepulse.bp.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author jiejiebiezheyang
 * @since 2025-03-28 16:00
 */
public class HlsUtils {


    /**
     * 根据m3u8文件获取视频时长
     */
    public static double getDuration(InputStream m3u8) throws IOException {
        double totalDuration = 0;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(m3u8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#EXTINF:")) {
                    int commaIndex = line.indexOf(',');
                    String durationStr = commaIndex > 0 ?
                            line.substring(8, commaIndex) : line.substring(8);
                    try {
                        totalDuration += Double.parseDouble(durationStr.trim());
                    } catch (NumberFormatException e) {
                        throw new IOException("Invalid duration format: " + line);
                    }
                }
            }
        }

        // 保留4位小数（四舍五入）
        BigDecimal bd = new BigDecimal(totalDuration);
        return bd.setScale(4, RoundingMode.HALF_UP).doubleValue();
    }
}
