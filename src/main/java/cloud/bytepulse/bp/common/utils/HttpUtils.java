package cloud.bytepulse.bp.common.utils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author jiejiebiezheyang
 * @since 2026-01-21 18:22
 */
public class HttpUtils {

        /**
         * 将 Map 转换为 URL 查询字符串
         * 支持 String、集合、数组类型的值
         *
         * @param params 参数 Map
         * @param repeatKey 是否使用重复 key 的方式（true: key=a&key=b；false: key=a,b）
         * @return query 字符串
         */
        public static String toQueryString(Map<String, Object> params, boolean repeatKey) {
            return params.entrySet().stream()
                    .flatMap(entry -> {
                        String key = entry.getKey();
                        Object value = entry.getValue();

                        switch (value) {
                            case null -> {
                                return Stream.empty();
                            }
                            case Iterable<?> iterable -> {
                                Stream<String> stream = StreamSupport.stream(iterable.spliterator(), false)
                                        .map(v -> encode(key, v.toString()));
                                return repeatKey ? stream : Stream.of(encode(key, join(iterable)));
                            }
                            case Object[] arr -> {
                                Stream<String> stream = Arrays.stream(arr)
                                        .map(v -> encode(key, v.toString()));
                                return repeatKey ? stream : Stream.of(encode(key, join(Arrays.asList(arr))));
                            }
                            default -> {
                                return Stream.of(encode(key, value.toString()));
                            }
                        }

                    })
                    .collect(Collectors.joining("&"));
        }

        private static String encode(String key, String value) {
            return URLEncoder.encode(key, StandardCharsets.UTF_8) + "=" +
                    URLEncoder.encode(value, StandardCharsets.UTF_8);
        }

        private static String join(Iterable<?> iterable) {
            return StreamSupport.stream(iterable.spliterator(), false)
                    .map(Object::toString)
                    .collect(Collectors.joining(","));
        }

        /**
         * 拼接 host 和 query
         *
         * @param host 基础地址，如 "<a href="https://api.example.com/search">https://api.example.com/search</a>"
         * @param params 参数 Map
         * @param repeatKey 是否使用重复 key 模式
         * @return 完整 URL
         */
        public static String buildUrl(String host, Map<String, Object> params, boolean repeatKey) {
            String query = toQueryString(params, repeatKey);
            if (query.isEmpty()) {
                return host;
            }

            // 去掉 host 末尾多余的斜杠（只在没有路径的情况下）
            if (host.endsWith("/")) {
                host = host.substring(0, host.length() - 1);
            }

            // 如果 host 已经包含 '?', 则追加 '&'，否则追加 '?'
            return host + (host.contains("?") ? "&" : "?") + query;
        }
}
