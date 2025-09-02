package cloud.bytepulse.common.core.util;

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
     * @param params    参数 Map
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
}
