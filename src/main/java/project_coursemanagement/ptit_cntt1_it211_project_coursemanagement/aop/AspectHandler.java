package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.aop;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class AspectHandler {
    // Ghi log thời gian thực hiện cho tất cả các chức năng



    @AfterThrowing(pointcut = " execution(* project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.*.*(..))", throwing = "exception")
    public void logExceptionCatch(JoinPoint joinPoint, Throwable exception){
        String methodName = joinPoint.getSignature().getName();
        log.error("[LOG] Ngoại lệ xảy ra tại phương thức {} : '{}'",methodName, exception.getMessage() );
    }
}
