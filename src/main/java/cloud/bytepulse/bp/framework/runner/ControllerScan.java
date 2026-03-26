package cloud.bytepulse.bp.framework.runner;


import cloud.bytepulse.bp.common.annotation.Anonymous;
import cloud.bytepulse.bp.common.annotation.ExternalApi;
import cloud.bytepulse.bp.common.annotation.NoLogging;
import cloud.bytepulse.bp.common.constant.AllHandlerConstant;
import cloud.bytepulse.bp.common.constant.AnonymousConstant;
import cloud.bytepulse.bp.common.constant.ExternalApiConstant;
import cloud.bytepulse.bp.common.constant.LoggingConstant;
import cloud.bytepulse.bp.framework.config.OpenAPIConfig;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 匿名接口扫描
 *
 * @author jiejiebiezheyang
 * @since 2024-06-26 00:00
 */
@Slf4j
@Component
public class ControllerScan implements BeanFactoryPostProcessor {


    private static final String[] BasePackages = {"cloud.bytepulse.**.controller"};

    /**
     * 扫描注解, 处理接口信息
     */
    @Override
    public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
        try {
            log.debug("开始扫描匿名接口...");
            Set<Class<?>> controllerClasses = scanControllers();

            for (Class<?> clazz : controllerClasses) {
                log.debug("扫描控制器类: {}", clazz.getName());

                String controllerPrefix = extractPath(clazz.getAnnotation(RequestMapping.class));

                // 添加 swagger 分组, 根据 @Tag description 的值作为分组名称
                if (clazz.isAnnotationPresent(Tag.class)) {
                    Tag tag = clazz.getAnnotation(Tag.class);
                    String desc = tag.description();
                    if (StringUtils.hasText(desc)) {
                        OpenAPIConfig.ApiGroup apiGroup = new OpenAPIConfig.ApiGroup(desc, controllerPrefix + "/**");
                        OpenAPIConfig.API_GROUPS.add(apiGroup);
                    }
                }

                for (Method method : clazz.getDeclaredMethods()) {
                    boolean hasAnonymous = method.isAnnotationPresent(Anonymous.class);
                    boolean hasNoLogging = method.isAnnotationPresent(NoLogging.class);
                    boolean hasExternalApi = method.isAnnotationPresent(ExternalApi.class);

                    String methodPath = extractPathFromMethod(method);
                    String fullPath = normalizePath(controllerPrefix, methodPath);
                    // 项目所有接口
                    AllHandlerConstant.ALL_HANDLER.add(fullPath);

                    // 添加所有需要日志的接口
                    if (!hasNoLogging) {
                        LoggingConstant.NEED_LOGGING.add(fullPath);
                    }
                    if (hasExternalApi && hasAnonymous) {
                        String fullMethodPath =
                                method.getDeclaringClass().getName()
                                        + "#"
                                        + method.getName();
                        throw new BeanCreationException(fullMethodPath + " 方法同时存在 @Anonymous 和 @ExternalApi 注解");
                    }
                    // 匿名放行接口
                    if (hasAnonymous) {
                        AnonymousConstant.ANONYMOUS.add(fullPath);
                        log.debug("添加匿名接口: {}", fullPath);
                    }
                    // 添加所有外部接口
                    if (hasExternalApi) {
                        AnonymousConstant.ANONYMOUS.add(fullPath);
                        ExternalApiConstant.EXTERNAL_API.add(fullPath);
                    }
                }
            }
            log.debug("匿名接口扫描完成: {}", AnonymousConstant.ANONYMOUS);
            AllHandlerConstant.ALL_HANDLER.addAll(AnonymousConstant.ANONYMOUS);
        } catch (Exception e) {
            throw new BeansException("匿名接口扫描失败: " + e.getMessage(), e) {
            };
        }
    }

    /**
     * 扫面控制器类
     */
    private Set<Class<?>> scanControllers() throws Exception {
        Set<Class<?>> classes = new HashSet<>();
        ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);

        scanner.addIncludeFilter(new AnnotationTypeFilter(RestController.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(Controller.class));

        for (String basePackage : BasePackages) {
            scanner.findCandidateComponents(basePackage).forEach(beanDefinition -> {
                try {
                    classes.add(Class.forName(beanDefinition.getBeanClassName()));
                } catch (ClassNotFoundException e) {
                    log.warn("无法加载类: {}", beanDefinition.getBeanClassName(), e);
                }
            });
        }
        return classes;
    }

    /**
     * 格式化路径
     */
    private String normalizePath(String prefix, String path) {
        String fullPath = (prefix + "/" + path)
                .replaceAll("//+", "/")
                .replaceAll("\\s+", "")
                .replaceAll("\\{[^}]+}", "*");

        if (fullPath.equals("/")) {
            return fullPath;
        }
        if (fullPath.endsWith("/")) {
            fullPath = fullPath.substring(0, fullPath.length() - 1);
        }
        return fullPath;
    }

    /**
     * 提取路径
     */
    private String extractPath(Annotation mapping) {
        if (mapping == null) return "";
        try {
            Method path = mapping.annotationType().getMethod("path");
            String[] paths = (String[]) path.invoke(mapping);
            if (paths.length > 0) return paths[0];

            Method value = mapping.annotationType().getMethod("value");
            String[] values = (String[]) value.invoke(mapping);
            if (values.length > 0) return values[0];
        } catch (Exception ignored) {
        }
        return "";
    }

    /**
     * 提取路径
     */
    private String extractPathFromMethod(Method method) {
        List<Class<? extends Annotation>> mappings = Arrays.asList(
                RequestMapping.class,
                GetMapping.class,
                PostMapping.class,
                PutMapping.class,
                DeleteMapping.class,
                PatchMapping.class
        );

        for (Class<? extends Annotation> annClass : mappings) {
            if (method.isAnnotationPresent(annClass)) {
                return extractPath(method.getAnnotation(annClass));
            }
        }
        return "";
    }
}
