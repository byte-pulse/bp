package cloud.bytepulse.bp.framework.exception.processor;

import cloud.bytepulse.bp.common.util.TraceIdUtils;
import cloud.bytepulse.bp.domain.ApiResponse;
import cloud.bytepulse.bp.framework.exception.BytePulseArgumentNotValidException;
import cloud.bytepulse.bp.framework.exception.BytePulseException;
import io.minio.errors.ErrorResponseException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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


    /**
     * 自定义业务务异常
     */
    @ResponseBody
    @ExceptionHandler(BytePulseException.class)
    public ApiResponse<Void> serviceException(BytePulseException ex) {
        Throwable cause = ex.getCause();
        if (cause != null) {
            log.warn("业务错误", cause);
        } else {
            log.warn("业务错误 [TraceId={}]: {}", TraceIdUtils.get(), ex.getMessage());
        }
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
    public ResponseEntity<ApiResponse<Void>> noResourceFoundException(NoResourceFoundException exception) {
        ApiResponse<Void> notFound = ApiResponse.notFound();
        notFound.message = "资源不存在: " + exception.getResourcePath();
        log.warn("资源不存在 [TraceId={}]: {}", TraceIdUtils.get(), exception.getResourcePath());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value())
                .body(notFound);
    }

    /**
     * 数据校验异常
     */
    @ResponseBody
    @ExceptionHandler({MethodArgumentNotValidException.class, BytePulseArgumentNotValidException.class})
    public ApiResponse<Void> validException(Exception ex) {
        String errMsg = "";
        if (ex instanceof MethodArgumentNotValidException e) {
            List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
            Set<String> msg = getStrings(allErrors);
            errMsg = msg.toString();
        } else if (ex instanceof BytePulseArgumentNotValidException e) {
            // 手动触发校验发生的异常
            errMsg = e.getMessage();
        }
        log.warn("接口参数校验失败 [TraceId={}]: {}", TraceIdUtils.get(), errMsg);
        return ApiResponse.badRequest(errMsg);
    }

    /**
     * 拼接 字段名和错误信息
     */
    private static @NonNull Set<String> getStrings(List<ObjectError> allErrors) {
        Set<String> msg = new HashSet<>();
        for (ObjectError error : allErrors) {
            String str = "";
            if (error instanceof FieldError fieldError) {
                String fieldName = fieldError.getField(); // 字段名
                String errorMsg = fieldError.getDefaultMessage(); // 错误信息
                str = String.format("[%s] %s", fieldName, errorMsg);
            } else {
                str = error.getDefaultMessage();
            }
            msg.add(str);
        }
        return msg;
    }

    /**
     * 数据访问错误
     */
    @ResponseBody
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<Void>> dataAccessException(DataAccessException ex) {
        log.error("数据访问错误 [TraceId={}]", TraceIdUtils.get(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(ApiResponse.error());
    }

    /**
     * SQL 错误
     */
    @ResponseBody
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ApiResponse<Void>> resolveException(SQLException ex) {
        log.error("SQL 错误 [TraceId={}]", TraceIdUtils.get(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(ApiResponse.error());
    }

    /**
     * 授权异常
     */
    @ResponseBody
    @ExceptionHandler({BadCredentialsException.class, InternalAuthenticationServiceException.class})
    public ResponseEntity<ApiResponse<Void>> badCredentialsException(Exception ex) {
        String msg = ex.getMessage();
        log.warn("授权登陆错误 [TraceId={}]: {}", TraceIdUtils.get(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED.value())
                .body(ApiResponse.unauthorized(msg));
    }

    /**
     * 权限异常
     */
    @ResponseBody
    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class, AuthorizationServiceException.class})
    public ResponseEntity<ApiResponse<Void>> accessDeniedException(Exception ex) {
        log.warn("权限错误 [TraceId={}]: {}", TraceIdUtils.get(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN.value())
                .body(ApiResponse.forbidden());
    }

    /**
     * Minio文件处理异常
     */
    @ResponseBody
    @ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<ApiResponse<Void>> minioException(ErrorResponseException ex) {
        log.error("Minio 文件处理异常 [TraceId={}]", TraceIdUtils.get(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(ApiResponse.error());
    }

    /**
     * JSON参数序列化异常
     */
    @ResponseBody
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> jsonNotReadableException(HttpMessageNotReadableException ex) {
        log.warn("请求 JSON 解析错误  [TraceId={}]: {}", TraceIdUtils.get(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                .body(ApiResponse.error());
    }

    /**
     * JSON参数序列化异常
     */
    @ResponseBody
    @ExceptionHandler(value = {MissingServletRequestParameterException.class, MissingServletRequestPartException.class})
    public ResponseEntity<ApiResponse<Void>> resolveMissingParameterException(Exception ex) {
        String missingParameterName = "";
        if (ex instanceof MissingServletRequestParameterException me) {
            missingParameterName = me.getParameterName();
        } else if (ex instanceof MissingServletRequestPartException me) {
            missingParameterName = me.getRequestPartName();
        }
        log.error("缺少必要参数 [TraceId={}]: {} ", TraceIdUtils.get(), missingParameterName, ex);
        ApiResponse<Void> error = ApiResponse.error("缺少参数: " + missingParameterName);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
                .body(error);
    }

    /**
     * 统一异常处理方法
     * 将异常进行统一处理,并将结果返回给前端
     */
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> resolveException(Exception ex) {
        log.error("未处理错误 [TraceId={}]", TraceIdUtils.get(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .body(ApiResponse.error());
    }
}
