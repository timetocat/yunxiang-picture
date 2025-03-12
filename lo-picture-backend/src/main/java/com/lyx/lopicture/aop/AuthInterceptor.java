package com.lyx.lopicture.aop;

import com.lyx.lopicture.annotation.AuthCheck;
import com.lyx.lopicture.common.BaseValueEnum;
import com.lyx.lopicture.exception.BusinessException;
import com.lyx.lopicture.exception.ErrorCode;
import com.lyx.lopicture.model.entity.User;
import com.lyx.lopicture.model.enums.UserRoleEnum;
import com.lyx.lopicture.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 鉴权拦截器
 */
@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        UserRoleEnum mustRoleEnum = BaseValueEnum.getEnumByValue(UserRoleEnum.class, mustRole);
        // 不需要权限，放行
        if (mustRoleEnum == null) {
            return joinPoint.proceed();
        }
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        User loginUser = userService.getLoginUser(request);
        UserRoleEnum userRoleEnum = BaseValueEnum.getEnumByValue(UserRoleEnum.class, loginUser.getUserRole());
        // 无权限，reject
        if (userRoleEnum == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 必须为管理员权限（即最高权限）
        if (UserRoleEnum.ADMIN == mustRoleEnum && UserRoleEnum.ADMIN != userRoleEnum) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // todo 权限包含和权限分配问题（RBAC模型）
        return joinPoint.proceed();
    }

}
