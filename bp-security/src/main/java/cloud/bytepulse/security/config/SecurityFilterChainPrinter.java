package cloud.bytepulse.security.config;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author jiejiebiezheyang
 * @since 2026-09-08 18:15
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityFilterChainPrinter implements ApplicationListener<ApplicationReadyEvent> {

    private final FilterChainProxy filterChainProxy;

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        List<SecurityFilterChain> chains = filterChainProxy.getFilterChains();

        StringBuilder sb = new StringBuilder();
        sb.append("\nSecurity Filter Chain:\n");

        for (int chainIndex = 0; chainIndex < chains.size(); chainIndex++) {
            SecurityFilterChain chain = chains.get(chainIndex);

            if (!(chain instanceof DefaultSecurityFilterChain dsc)) {
                continue;
            }

            List<Filter> filters = dsc.getFilters();

            int orderWidth = Math.max(
                    5,
                    String.valueOf(filters.size() - 1).length()
            );

            int nameWidth = Math.max(
                    "Name".length(),
                    filters.stream()
                            .map(f -> f.getClass().getSimpleName())
                            .mapToInt(String::length)
                            .max()
                            .orElse(0)
            );

            int classWidth = Math.max(
                    "Class".length(),
                    filters.stream()
                            .map(f -> f.getClass().getName())
                            .mapToInt(String::length)
                            .max()
                            .orElse(0)
            );

            String format = "| %-" + orderWidth + "s | %-" + nameWidth + "s | %-" + classWidth + "s |%n";

            String separator =
                    "+" + "-".repeat(orderWidth + 2)
                            + "+" + "-".repeat(nameWidth + 2)
                            + "+" + "-".repeat(classWidth + 2)
                            + "+";

            sb.append("\nChain #").append(chainIndex).append('\n');
            sb.append(separator).append('\n');
            sb.append(String.format(format, "Order", "Name", "Class"));
            sb.append(separator).append('\n');

            for (int j = 0; j < filters.size(); j++) {
                Filter filter = filters.get(j);

                sb.append(String.format(
                        format,
                        j,
                        filter.getClass().getSimpleName(),
                        filter.getClass().getName()
                ));
            }

            sb.append(separator).append('\n');
        }

        log.debug("{}", sb);
    }
}