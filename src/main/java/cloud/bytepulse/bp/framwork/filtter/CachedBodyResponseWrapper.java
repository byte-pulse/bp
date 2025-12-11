package cloud.bytepulse.bp.framwork.filtter;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.*;

public class CachedBodyResponseWrapper extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    private ServletOutputStream outputStream;
    private PrintWriter writer;
    private final HttpServletResponse originalResponse;

    public CachedBodyResponseWrapper(HttpServletResponse response) {
        super(response);
        this.originalResponse = response;
    }

    @Override
    public ServletOutputStream getOutputStream() {
        if (outputStream == null) {
            outputStream = new ServletOutputStream() {

                private final OutputStream bufferStream = buffer;

                @Override
                public void write(int b) throws IOException {
                    bufferStream.write(b);
                }

                @Override
                public void setWriteListener(WriteListener listener) {}

                @Override
                public boolean isReady() {
                    return true;
                }
            };
        }
        return outputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer == null) {
            writer = new PrintWriter(
                    new OutputStreamWriter(buffer, originalResponse.getCharacterEncoding()),
                    true
            );
        }
        return writer;
    }

    public byte[] getBody() throws IOException {
        if (writer != null) writer.flush();
        if (outputStream != null) outputStream.flush();
        return buffer.toByteArray();
    }

    public void setBody(byte[] newBody) throws IOException {
        buffer.reset();
        buffer.write(newBody);
    }

    public void copyToResponse() throws IOException {
        byte[] content = getBody();

        originalResponse.setContentLength(content.length);
        ServletOutputStream out = originalResponse.getOutputStream();
        out.write(content);
        out.flush();
    }
}
