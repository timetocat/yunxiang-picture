package com.lyx.lopicture.aop;

import com.lyx.lopicture.common.Validator;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ValidatorInterceptor {

    // 参数校验
    @Before("execution(* com.lyx.lopicture.service.impl.*.*(..)) " + "&&" +
            " !target(com.baomidou.mybatisplus.extension.service.impl.ServiceImpl) ")
    public void doBefore(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof Validator) {
                ((Validator) arg).validate();
            }
        }
    }
}
