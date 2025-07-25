package cloud.bytepulse.common.core.exception;

import cloud.bytepulse.common.core.exception.enums.interfaces.ErrorCode;
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
}
