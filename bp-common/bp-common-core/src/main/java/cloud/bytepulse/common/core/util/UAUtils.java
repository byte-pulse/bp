package cloud.bytepulse.common.core.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * UAParserUtil - 单文件 User-Agent 解析工具类，支持新版 Edge (Edg/...) 喵~
 * 使用示例：
 * <pre>
 * String ua = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
 *             "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 " +
 *             "Safari/537.36 Edg/139.0.0.0";
 * UAParserUtil.UAResult result = UAParserUtil.parse(ua);
 * System.out.println(result);
 * </pre>
 *
 * @author jiejiebiezheyang
 * @since 2023-05-20 21:31
 */
public class UAUtils {

    /**
     * 解析 UA 并返回 UAResult
     */
    public static UAResult parse(String ua) {
        ua = ua.toLowerCase();
        return UAResult.builder()
                .browserName(parseBrowserName(ua))
                .browserVersion(parseBrowserVersion(ua))
                .osName(parseOSName(ua))
                .osVersion(parseOSVersion(ua))
                .deviceType(parseDevice(ua))
                .engine(parseEngine(ua))
                .build();
    }

    private static String parseBrowserName(String ua) {
        if (ua.contains("edg/")) return "Edge";
        if (ua.contains("chrome") && !ua.contains("edge") && !ua.contains("opr")) return "Chrome";
        if (ua.contains("firefox")) return "Firefox";
        if (ua.contains("safari") && !ua.contains("chrome")) return "Safari";
        if (ua.contains("opr") || ua.contains("opera")) return "Opera";
        if (ua.contains("msie") || ua.contains("trident")) return "IE";
        return "Unknown";
    }

    private static String parseBrowserVersion(String ua) {
        Pattern pattern;
        Matcher matcher;

        if (ua.contains("edg/")) {
            pattern = Pattern.compile("edg/([\\d\\.]+)");
            matcher = pattern.matcher(ua);
            if (matcher.find()) return matcher.group(1);
        } else if (ua.contains("chrome") && !ua.contains("edge") && !ua.contains("opr")) {
            pattern = Pattern.compile("chrome/([\\d\\.]+)");
            matcher = pattern.matcher(ua);
            if (matcher.find()) return matcher.group(1);
        } else if (ua.contains("firefox")) {
            pattern = Pattern.compile("firefox/([\\d\\.]+)");
            matcher = pattern.matcher(ua);
            if (matcher.find()) return matcher.group(1);
        } else if (ua.contains("safari") && !ua.contains("chrome")) {
            pattern = Pattern.compile("version/([\\d\\.]+)");
            matcher = pattern.matcher(ua);
            if (matcher.find()) return matcher.group(1);
        } else if (ua.contains("opr") || ua.contains("opera")) {
            pattern = Pattern.compile("opr/([\\d\\.]+)");
            matcher = pattern.matcher(ua);
            if (matcher.find()) return matcher.group(1);
        } else if (ua.contains("msie") || ua.contains("trident")) {
            pattern = Pattern.compile("msie ([\\d\\.]+);");
            matcher = pattern.matcher(ua);
            if (matcher.find()) return matcher.group(1);
            pattern = Pattern.compile("rv:([\\d\\.]+)");
            matcher = pattern.matcher(ua);
            if (matcher.find()) return matcher.group(1);
        }
        return "Unknown";
    }
}
