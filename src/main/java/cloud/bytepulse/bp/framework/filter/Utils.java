package cloud.bytepulse.bp.framework.filter;

import cloud.bytepulse.bp.common.util.json.JsonUtils;
import cloud.bytepulse.bp.domain.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author jiejiebiezheyang
 * @since 2026-01-20 12:21
 */
public class Utils {

    /**
     * 401 输出
     */
    public static void printUnauthorized(HttpServletResponse response, String msg) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(JsonUtils.toJsonStr(ApiResponse.unauthorized(msg)));
    }

    /**
     * 404 输出
     *
     */
    public static void printNotFound(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        ApiResponse<Void> notFound = ApiResponse.notFound();
        notFound.message = "资源不存在:" + request.getRequestURI();
        response.getWriter().println(JsonUtils.toJsonStr(notFound));
    }
}
