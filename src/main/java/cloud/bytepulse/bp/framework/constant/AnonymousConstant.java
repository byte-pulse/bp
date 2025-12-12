package cloud.bytepulse.bp.framework.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jiejiebiezheyang
 * @since 2025-04-21 17:00
 */
public class AnonymousConstant {

    public final static List<String> ANONYMOUS = new ArrayList<>();

    static {
        ANONYMOUS.add("/favicon.ico");
        ANONYMOUS.add("/swagger-ui.html");
        ANONYMOUS.add("/swagger-ui/*");
        ANONYMOUS.add("/swagger-resources/**");
        ANONYMOUS.add("/v2/api-docs");
        ANONYMOUS.add("/v3/api-docs");
        ANONYMOUS.add("/v3/api-docs/**");
        ANONYMOUS.add("/doc.html");
        ANONYMOUS.add("/META-INF/resources/webjars/**");
        ANONYMOUS.add("/druid/**");
        ANONYMOUS.add("/actuator/**");
        ANONYMOUS.add("/error");
    }
}
