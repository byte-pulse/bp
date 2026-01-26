package cloud.bytepulse.bp.common.util;

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
public class UAParserUtils {

    @Data
    @AllArgsConstructor
    @Builder
    public static class UAResult {
        private String browserName;     // 浏览器名称
        private String browserVersion;  // 浏览器版本号
        private String osName;          // 操作系统名称
        private String osVersion;       // 操作系统版本号
        private DeviceType deviceType;  // 设备类型
        private Engine engine;          // 浏览器内核

        /**
         * 简短信息
         */
        public String shortInfo() {
            return osName + " " + osVersion + " " + browserName + " " + browserVersion;
        }
    }

    public enum DeviceType {MOBILE, TABLET, DESKTOP, UNKNOWN}

    public enum Engine {WEBKIT, GECKO, TRIDENT, BLINK, UNKNOWN}

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

    private static String parseOSName(String ua) {
        if (ua.contains("windows")) return "Windows";
        if (ua.contains("mac os x") || ua.contains("macintosh")) return "MacOS";
        if (ua.contains("android")) return "Android";
        if (ua.contains("iphone os") || ua.contains("ios")) return "iOS";
        if (ua.contains("linux")) return "Linux";
        return "Unknown";
    }

    private static String parseOSVersion(String ua) {
        Pattern pattern;
        Matcher matcher;

        if (ua.contains("windows")) {
            pattern = Pattern.compile("windows nt ([\\d\\.]+)");
            matcher = pattern.matcher(ua);
            if (matcher.find()) return matcher.group(1);
        } else if (ua.contains("mac os x") || ua.contains("macintosh")) {
            pattern = Pattern.compile("mac os x ([\\d_\\.]+)");
            matcher = pattern.matcher(ua);
            if (matcher.find()) return matcher.group(1).replace("_", ".");
        } else if (ua.contains("android")) {
            pattern = Pattern.compile("android ([\\d\\.]+)");
            matcher = pattern.matcher(ua);
            if (matcher.find()) return matcher.group(1);
        } else if (ua.contains("iphone os") || ua.contains("ios")) {
            pattern = Pattern.compile("iphone os ([\\d_]+)");
            matcher = pattern.matcher(ua);
            if (matcher.find()) return matcher.group(1).replace("_", ".");
        }
        return "Unknown";
    }

    private static DeviceType parseDevice(String ua) {
        if (ua.contains("mobile") || ua.contains("iphone") || ua.contains("android")) return DeviceType.MOBILE;
        if (ua.contains("ipad") || ua.contains("tablet")) return DeviceType.TABLET;
        if (ua.contains("windows") || ua.contains("macintosh") || ua.contains("linux")) return DeviceType.DESKTOP;
        return DeviceType.UNKNOWN;
    }

    private static Engine parseEngine(String ua) {
        if (ua.contains("webkit")) return Engine.WEBKIT;
        if (ua.contains("gecko") && !ua.contains("like gecko")) return Engine.GECKO;
        if (ua.contains("trident")) return Engine.TRIDENT;
        if (ua.contains("blink")) return Engine.BLINK;
        return Engine.UNKNOWN;
    }
}
