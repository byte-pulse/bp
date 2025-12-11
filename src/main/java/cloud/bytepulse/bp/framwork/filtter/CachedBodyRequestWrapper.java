package cloud.bytepulse.bp.framwork.filtter;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.*;

public class CachedBodyRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] body;

    public CachedBodyRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        body = request.getInputStream().readAllBytes();
    }

    @Override
    public ServletInputStream getInputStream() {
        ByteArrayInputStream bais = new ByteArrayInputStream(body);

        return new ServletInputStream() {
            @Override
            public int read() {
                return bais.read();
            }

            @Override
            public boolean isFinished() {
                return bais.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener listener) {}
        };
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(
                new InputStreamReader(
                        new ByteArrayInputStream(body),
                        this.getCharacterEncoding() != null ? this.getCharacterEncoding() : "UTF-8"
                )
        );
    }

    public String getBodyString() throws IOException {
        return new String(body, this.getCharacterEncoding() != null ? this.getCharacterEncoding() : "UTF-8");
    }
}
