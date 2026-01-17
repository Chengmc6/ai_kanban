package com.example.ai_kanban.aop.aspect;

import com.example.ai_kanban.common.ResultCode;
import com.example.ai_kanban.common.exception.SystemException;
import com.example.ai_kanban.aop.annotation.ParameterNotNull;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Parameter;

/**
 * AOP 兜底校验：
 * - 仅用于防止 null 参数进入业务层
 * - 不替代 Bean Validation
 */

@Aspect
@Component
public class ValidateNoNullAspect {

    @Before("execution(* com.example.ai_kanban..service..*(..))")
    public void checkNullParameter(JoinPoint joinPoint){
        Object[] args= joinPoint.getArgs();
        MethodSignature signature=(MethodSignature) joinPoint.getSignature();
        Parameter[] parameters=signature.getMethod().getParameters();

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter=parameters[i];
            if(parameter.isAnnotationPresent(ParameterNotNull.class)){
                Object value=args[i];
                if(value==null){
                    throw new SystemException(ResultCode.BAD_REQUEST);
                }
            }
        }
    }
}
