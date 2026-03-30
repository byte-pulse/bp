package cloud.bytepulse.bp.framework.exception.processor;

import cloud.bytepulse.bp.domain.ApiResponse;
import cloud.bytepulse.bp.framework.exception.BytePulseArgumentNotValidException;
import cloud.bytepulse.bp.framework.exception.BytePulseException;
import cloud.bytepulse.bp.framework.properties.AppProperties;
import io.minio.errors.ErrorResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 统一异常处理器
 *
 * @author jiejiebiezheyang
 * @since 2023-04-03 15:40
 */
@Slf4j
@Order(-999)
@ControllerAdvice
public class ExceptionProcessor {

    private final Boolean debug;

    public ExceptionProcessor(AppProperties appProperties) {
        this.debug = appProperties.getExProcesser().getDebugInfo();
    }

    /**
     * 自定义业务务异常
     */
    @ResponseBody
    @ExceptionHandler(BytePulseException.class)
    public ApiResponse<Void> resolveException(BytePulseException ex) {
        log.error("自定义业务务异常", ex);
        ApiResponse<Void> error = ApiResponse.error(ex.getMessage());
        if (ex.getErrorCode() > 0) {
            error.code = ex.getErrorCode();
        }
        return error;
    }

    /**
     * 404错误页面
     */
    @ResponseBody
    @ExceptionHandler(NoResourceFoundException.class)
    public ApiResponse<Void> noResourceFoundException(NoResourceFoundException exception) {
        ApiResponse<Void> notFound = ApiResponse.notFound();
        notFound.message = "资源不存在: " + exception.getResourcePath();
        return notFound;
    }

    /**
     * 数据校验异常
     */
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> resolveException(MethodArgumentNotValidException ex) {
        log.error("数据校验异常", ex);
        List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();
        Set<String> msg = new HashSet<>();
        for (ObjectError error : allErrors) {
            String str = "";
            if (error instanceof FieldError fieldError) {
                String fieldName = fieldError.getField(); // 字段名
                String errorMsg = fieldError.getDefaultMessage(); // 错误信息
                str = String.format("[%s] %s", fieldName, errorMsg);
            } else {
                str = String.format("[%s] %s", error.getObjectName(), error.getDefaultMessage());
            }
            msg.add(str);
        }
        return ApiResponse.badRequest(msg.toString());
    }

    /**
     * 数据校验异常
     */
    @ResponseBody
    @ExceptionHandler(BytePulseArgumentNotValidException.class)
    public ApiResponse<Void> resolveException(BytePulseArgumentNotValidException ex) {
        log.error("数据校验异常", ex);
        return ApiResponse.badRequest(ex.getMessage());
    }

    /**
     * sql 错误
     */
    @ResponseBody
    @ExceptionHandler(DataAccessException.class)
    public ApiResponse<Void> resolveException(DataAccessException ex) {
        log.error("数据库查询异常", ex);
        ApiResponse<Void> error = ApiResponse.error();
        if (debug) {
            error.e = ex.getMessage();
        }
        return error;
    }

    /**
     * sql 错误
     */
    @ResponseBody
    @ExceptionHandler(SQLException.class)
    public ApiResponse<Void> resolveException(SQLException ex) {
        log.error("数据库查询异常", ex);
        ApiResponse<Void> error = ApiResponse.error();
        if (debug) {
            error.e = ex.getMessage();
        }
        return error;
    }

    /**
     * 授权异常
     */
    @ResponseBody
    @ExceptionHandler({BadCredentialsException.class, InternalAuthenticationServiceException.class})
    public ApiResponse<Void> badCredentialsException(Exception ex) {
        String msg = "";
        log.error("授权异常", ex);
        if (ex instanceof BadCredentialsException me) {
            msg = me.getMessage();
        } else if (ex instanceof InternalAuthenticationServiceException me) {
            msg = me.getMessage();
        }
        return ApiResponse.unauthorized(msg);
    }

    /**
     * 权限异常
     */
    @ResponseBody
    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class, AuthorizationServiceException.class})
    public ApiResponse<Void> resolveException(AccessDeniedException ex) {
        log.error("权限异常", ex);
        return ApiResponse.forbidden();
    }

    /**
     * Minio文件处理异常
     */
    @ResponseBody
    @ExceptionHandler(ErrorResponseException.class)
    public ApiResponse<Void> resolveException(ErrorResponseException ex) {
        log.error("文件处理异常", ex);
        ApiResponse<Void> error = ApiResponse.error();
        if (debug) {
            error.e = ex.getMessage();
        }
        return error;
    }

    /**
     * JSON参数序列化异常
     */
    @ResponseBody
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse<Void> resolveException(HttpMessageNotReadableException ex) {
        log.error("JSON参数异常", ex);
        ApiResponse<Void> error = ApiResponse.error("JSON参数异常");
        if (debug) {
            error.e = ex.getMessage();
        }
        return error;
    }

    /**
     * JSON参数序列化异常
     */
    @ResponseBody
    @ExceptionHandler(value = {MissingServletRequestParameterException.class, MissingServletRequestPartException.class})
    public ApiResponse<Void> resolveMissingParameterException(Exception ex) {
        String missingParameterName = "";
        if (ex instanceof MissingServletRequestParameterException me) {
            missingParameterName = me.getParameterName();
        } else if (ex instanceof MissingServletRequestPartException me) {
            missingParameterName = me.getRequestPartName();
        }
        log.error("缺少必要参数: {}", missingParameterName, ex);
        ApiResponse<Void> error = ApiResponse.error("缺少必要参数: " + missingParameterName);
        if (debug) {
            error.e = ex.getMessage();
        }
        return error;
    }

    /**
     * 统一异常处理方法
     * 将异常进行统一处理,并将结果返回给前端
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> resolveException(Exception ex) {
        log.error("Exception异常捕获", ex);
        ApiResponse<Void> error = ApiResponse.error();
        if (debug) {
            error.e = ex.getMessage();
        }
        return error;
    }
}
