package cloud.bytepulse.web.controller;


import cloud.bytepulse.common.core.annotation.Anonymous;
import cloud.bytepulse.common.core.annotation.BpLogging;
import cloud.bytepulse.common.core.annotation.RequestLimit;
import cloud.bytepulse.common.core.enums.OperateEnum;
import cloud.bytepulse.common.core.model.ApiResponse;
import cloud.bytepulse.service.auth.AuthService;
import cloud.bytepulse.service.auth.dto.LoginDTO;
import cloud.bytepulse.service.auth.vo.LoginResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证
 *
 * @author jiejiebiezheyang
 * @since 2024-03-03 11:00
 */
@Tag(name = "登录认证")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

}
