package cloud.bytepulse.bp.framework.http.wrapper;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author jiejiebiezheyang
 * @since 2026-01-20 12:56
 * <p>
 * 作用:
 * 1. 缓存 HttpServletRequest 的 body
 * 2. 支持多次读取 request body
 * 3. 典型使用场景: Filter / Interceptor 中做签名校验
 * <p>
 * 注意:
 * - Servlet 规范中 request.getInputStream() 只能读一次
 * - 如果你在 Filter 里读了 body, Controller 就拿不到了
 * - 这个 Wrapper 就是为了解决这个问题
 */
@Getter
public class CachedBodyRequestWrapper extends HttpServletRequestWrapper {

    /**
     * 缓存的 request body 字节数组
     * 一旦构造完成, 后续所有读取都来自这里
     * -- GETTER --
     * 直接获取缓存的 body
     * <p>
     * 这个方法非常适合:
     * - Filter 中验签
     * - 打日志(注意脱敏)
     *
     * @return body 的字节数组
     */
    private final byte[] cachedBody;

    /**
     * 构造方法
     *
     * @param request 原始 HttpServletRequest
     * @throws IOException 读取输入流异常
     */
    public CachedBodyRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);

        // 这里是关键点:
        // 第一次从原始 request 中把 body 全部读出来
        // 并且缓存到内存中
        cachedBody = request.getInputStream().readAllBytes();
    }

    /**
     * 重写 getInputStream()
     * <p>
     * 每次调用都返回一个新的 ServletInputStream
     * 底层数据来自 cachedBody
     * <p>
     * 这样就实现了:
     * - Filter 能读
     * - Controller 还能再读
     */
    @Override
    public ServletInputStream getInputStream() {

        // 使用 ByteArrayInputStream 包装缓存的 body
        ByteArrayInputStream byteArrayInputStream =
                new ByteArrayInputStream(cachedBody);

        return new ServletInputStream() {

            @Override
            public boolean isFinished() {
                // 当没有剩余可读字节时返回 true
                return byteArrayInputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                // 这里直接返回 true 即可
                // 同步 IO 场景不需要复杂处理
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // 异步 IO 场景才会用到
                // 大多数 API 网关 / 后端接口可以直接留空
            }

            @Override
            public int read() {
                // 实际读取数据的地方
                return byteArrayInputStream.read();
            }
        };
    }

    /**
     * 重写 getReader()
     * <p>
     * 有些框架是通过 reader 读 body 的
     * 所以这里也必须一起重写
     */
    @Override
    public BufferedReader getReader() {
        return new BufferedReader(
                new InputStreamReader(getInputStream(), StandardCharsets.UTF_8)
        );
    }

    /**
     * 获取 body 的字符串形式
     * <p>
     * 注意:
     * - 默认使用 UTF-8
     * - 只适合 JSON / 文本类型请求
     */
    public String getCachedBodyAsString() {
        return new String(cachedBody, StandardCharsets.UTF_8);
    }
}
