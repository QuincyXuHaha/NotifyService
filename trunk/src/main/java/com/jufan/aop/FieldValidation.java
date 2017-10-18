package com.jufan.aop;

import com.jufan.model.CommonResp;
        import com.jufan.util.CommonUtil;
        import org.aspectj.lang.ProceedingJoinPoint;
        import org.aspectj.lang.annotation.Around;
        import org.aspectj.lang.annotation.Aspect;
        import org.aspectj.lang.annotation.Pointcut;
        import org.springframework.stereotype.Component;
        import org.springframework.validation.Errors;

/**
 *
 * 一个普通的切面，可以用来校验Controller的入参
 *
 * 使用方式：在Controller的方法编写时按照下面的格式即可生效：
 *
 * public XX method(@{@link javax.validation.Valid} Model model, {@link Errors} errors) {...}
 *
 * 把{@link Errors}放在参数的最后一个即可匹配到切点
 *
 * 然后在Model中使用validation系列的注解即可
 *
 * @author 李尧
 * @since  0.3.0
 */
@Component
@Aspect
public class FieldValidation {

    @Pointcut("execution(* com.jufan.controller.*.*(.., org.springframework.validation.Errors))")
    public void aspect() {}

    @Around("aspect()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Object[] objects = pjp.getArgs();
        Errors errors = (Errors) objects[objects.length - 1];
        if (errors.hasErrors()) {
            String msg = CommonUtil.errorMessage(errors);
            return new CommonResp(1, msg);
        }
        return pjp.proceed();
    }
}
