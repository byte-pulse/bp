package cloud.bytepulse.bp.framework.runner;

import cloud.bytepulse.bp.common.enums.errorcode.ErrorCode;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 启动时检查错误码是否重复
 *
 * @author jiejiebiezheyang
 * @since 2026-03-31 20:57
 */
@Component
public class ErrorCodeChecker implements ApplicationRunner {

    private final BeanFactory beanFactory;

    public ErrorCodeChecker(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    /**
     * Callback used to run the bean.
     *
     * @param args incoming application arguments
     * @throws Exception on error
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {

        // key: 错误码
        // value: 所有出现的位置
        Map<Integer, List<String>> codeMap = new HashMap<>();

        // 获取启动类所在包
        List<String> basePackages = new ArrayList<>();
        if (AutoConfigurationPackages.has(beanFactory)) {
            basePackages.addAll(AutoConfigurationPackages.get(beanFactory));
        }

        // 创建扫描器
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);

        scanner.addIncludeFilter(new AssignableTypeFilter(ErrorCode.class));

        // 遍历每个基础包
        for (String basePackage : basePackages) {

            Set<BeanDefinition> beanDefinitions =
                    scanner.findCandidateComponents(basePackage);

            for (BeanDefinition beanDefinition : beanDefinitions) {

                Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());

                if (!clazz.isEnum()) continue;

                Object[] enumConstants = clazz.getEnumConstants();

                for (Object obj : enumConstants) {

                    ErrorCode errorCode = (ErrorCode) obj;

                    int code = errorCode.code();

                    // 当前枚举位置 (类名 + 枚举名)
                    String location = clazz.getName() + "." + ((Enum<?>) obj).name();

                    // 如果没有这个 code, 初始化一个 list
                    codeMap.computeIfAbsent(code, k -> new ArrayList<>())
                            .add(location);
                }
            }
        }

        // 收集所有重复项
        List<String> duplicateMessages = new ArrayList<>();

        for (Map.Entry<Integer, List<String>> entry : codeMap.entrySet()) {

            int code = entry.getKey();
            List<String> locations = entry.getValue();

            // 只要出现次数 > 1 就是重复
            if (locations.size() > 1) {

                StringBuilder sb = new StringBuilder();
                sb.append("错误码 ").append(code).append(" 重复 (")
                        .append(locations.size()).append(" 处):\n");

                for (String loc : locations) {
                    sb.append("    - ").append(loc).append("\n");
                }

                duplicateMessages.add(sb.toString());
            }
        }

        // 如果存在重复, 一次性抛出
        if (!duplicateMessages.isEmpty()) {

            throw new IllegalStateException(
                    "\n============================= 错误码重复检测失败 =============================\n" +
                            String.join("\n", duplicateMessages) +
                            "\n============================================================================"
            );
        }
    }
}
