package cloud.bytepulse.bp.framwork.filtter;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 可重复读取响应体的包装器
 */
public class CachedBodyResponseWrapper extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private ServletOutputStream outputStream;
    private PrintWriter writer;

    public CachedBodyResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() {
        if (outputStream == null) {
            outputStream = new ServletOutputStream() {
                @Override
                public void write(int b) {
                    buffer.write(b);
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setWriteListener(WriteListener listener) {
                }
            };
        }
        return outputStream;
    }

    @Override
    public PrintWriter getWriter() {
        if (writer == null) {
            writer = new PrintWriter(buffer, true);
        }
        return writer;
    }

    /**
     * 覆盖响应体内容
     */
    public void setBody(byte[] newBody) throws IOException {
        buffer.reset();        // 清空原 buffer
        buffer.write(newBody); // 写入新的字节数组
    }


    /**
     * 获取响应体内容
     */
    public byte[] getBody() throws IOException {
        if (writer != null) {
            writer.flush();
        }
        if (outputStream != null) {
            outputStream.flush();
        }
        return buffer.toByteArray();
    }

    /**
     * 写回响应给客户端
     */
    public void copyToResponse() throws IOException {
        HttpServletResponse response = (HttpServletResponse) getResponse();
        byte[] bytes = getBody();
        response.getOutputStream().write(bytes);
        response.getOutputStream().flush();
    }
}
