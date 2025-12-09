package cloud.bytepulse.bp.common.utils;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import lombok.Data;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

/**
 * nginx工具类
 * <p>
 * # 传递真实客户端IP相关头信息
 * proxy_set_header Host $host;
 * proxy_set_header X-Real-IP $remote_addr;
 * proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
 * proxy_set_header X-Forwarded-Proto $scheme;
 * <p>
 * # 其他需要传递的头
 * proxy_set_header Proxy-Client-IP $remote_addr;
 * proxy_set_header WL-Proxy-Client-IP $remote_addr;
 * proxy_set_header HTTP_CLIENT_IP $remote_addr;
 * proxy_set_header HTTP_X_FORWARDED_FOR $proxy_add_x_forwarded_for;
 * <p>
 * # 保持原始User-Agent
 * proxy_set_header User-Agent $http_user_agent;
 *
 * @author jiejiebiezheyang
 * @since 2023-05-20 21:31
 */
public class ReqUtils {

    /**
     * 配置了nginx反向代理后,获取ip
     */
    public static String getIP(HttpServletRequest request) {
        String ip = null;
        ip = request.getHeader("x-forwarded-for");
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        //使用代理，则获取第一个IP地址
        if (ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        return ip;
    }

    /**
     * 获取ip
     */
    public static String getIP() {
        return getIP(getRequest());
    }

    /**
     * 获取当前请求对象
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null;
        return requestAttributes.getRequest();
    }

    /**
     * 路径是否匹配
     */
    public static boolean isPathMatching(List<String> paths, String apiPath) {
        AntPathMatcher matcher = new AntPathMatcher();
        for (String path : paths) {
            if (matcher.match(path, apiPath)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取设备信息
     */
    public static String getDeviceInfo(String userAgent) {
        UAParserUtil.UAResult result = UAParserUtil.parse(userAgent);
        return result.shortInfo();
    }

    /**
     * 获取设备信息
     */
    public static String getDeviceInfo() {
        return getDeviceInfo(getUserAgentInfo());
    }

    /**
     * 获取userAgent信息
     *
     */
    public static String getUserAgentInfo(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }

    /**
     * 获取userAgent信息
     *
     */
    public static String getUserAgentInfo() {
        return getUserAgentInfo(getRequest());
    }

    /**
     * 获取ip地理信息
     */
    public static IpGeoInfo getIpFeoInfo() {
        return getIpFeoInfo(getIP());
    }

    /**
     * 获取ip地理信息
     */
    public static IpGeoInfo getIpFeoInfo(String ip) {
        String url = "https://ip9.com.cn/get?ip=" + ip;
        HttpResponse<String> ipGeoInfoHttpResponse = Unirest.get(url)
                .asString();
        if (ipGeoInfoHttpResponse.getStatus() == 200) {
            String body = ipGeoInfoHttpResponse.getBody();
            ObjectMapper mapper = new ObjectMapper();
            try {
                JsonNode jsonNode = mapper.readTree(body);
                JsonNode data = jsonNode.get("data");
                return mapper.treeToValue(data, IpGeoInfo.class);
            } catch (JsonProcessingException e) {
                return new IpGeoInfo();
            }
        }
        return new IpGeoInfo();
    }


    /**
     * IP地理位置信息
     */
    @Data
    public static class IpGeoInfo {
        /**
         * IP地址
         */
        private String ip;

        /**
         * 国家名称
         */
        private String country;

        /**
         * 国家代码
         */
        @JsonProperty("country_code")
        private String countryCode;

        /**
         * 省份/州
         */
        private String prov;

        /**
         * 城市名称
         */
        private String city;

        /**
         * 城市代码
         */
        @JsonProperty("city_code")
        private String cityCode;

        /**
         * 城市简称
         */
        @JsonProperty("city_short_code")
        private String cityShortCode;

        /**
         * 区/县
         */
        private String area;

        /**
         * 邮政编码
         */
        @JsonProperty("post_code")
        private String postCode;

        /**
         * 区号
         */
        @JsonProperty("area_code")
        private String areaCode;

        /**
         * 网络服务提供商
         */
        private String isp;

        /**
         * 经度
         */
        private String lng;

        /**
         * 纬度
         */
        private String lat;

        /**
         * IP长整型表示
         */
        @JsonProperty("long_ip")
        private Long longIp;

        /**
         * 大区(如华东、华北等)
         */
        @JsonProperty("big_area")
        private String bigArea;

        /**
         * 获取简短的地理位置信息
         */
        public String getShortInfo() {
            switch (this.isp) {
                case "内网地址":
                    return "内网";
                case "回环地址":
                    return "本机";
                default:
                    String prov = this.prov == null ? "未知" : this.prov;
                    String city = this.city == null ? "未知" : this.city;
                    String area = this.area == null ? "未知" : this.area;
                    return prov + " " + city + " " + area;
            }
        }
    }
}
