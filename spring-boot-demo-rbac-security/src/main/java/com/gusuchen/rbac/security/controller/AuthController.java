package com.gusuchen.rbac.security.controller;

import com.gusuchen.rbac.security.common.ApiResponse;
import com.gusuchen.rbac.security.common.IStatus;
import com.gusuchen.rbac.security.common.Status;
import com.gusuchen.rbac.security.exception.SecurityException;
import com.gusuchen.rbac.security.payload.LoginRequest;
import com.gusuchen.rbac.security.util.JwtUtil;
import com.gusuchen.rbac.security.vo.JwtResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * <p></p>
 *
 * @author gusuchen
 * @version V1.0
 * @date Created in 2019-09-17 18:03
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/auth")
public class AuthController {

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private JwtUtil jwtUtil;

    /**
     * 登录
     *
     * @param loginRequest 登录请求
     * @return {@link JwtResponse}
     */
    @PostMapping(value = "/login")
    public ApiResponse<JwtResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmailOrPhone(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.createJwt(authentication, loginRequest.getRememberMe());
        return ApiResponse.ofSuccess(new JwtResponse(jwt));
    }

    /**
     * 登出
     *
     * @param request {@link HttpServletRequest}
     * @return {@link IStatus}
     */
    @PostMapping(value = "/logout")
    public ApiResponse<IStatus> logout(HttpServletRequest request) {
        try {
            jwtUtil.invalidateJwt(request);
        } catch (SecurityException e) {
            throw new SecurityException(Status.UNAUTHORIZED);
        }

        return ApiResponse.ofStatus(Status.LOGOUT);
    }
}
