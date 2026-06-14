package project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.aop;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class AspectHandler {

    // Ghi log thời gian thực hiện cho tất cả các chức năng
    @Around(" execution(* project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.controller..*(..)) || execution(* project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.impl..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        long startTime = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long endTime = System.currentTimeMillis();

        long executionTime = endTime - startTime;

        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        log.info("[LOG] Class: {} | Method: {} | Execution Time: {} ms", className, methodName, executionTime);

        return result;
    }

    // Log exception
    @AfterThrowing(pointcut = """
            execution(* project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service..*(..))
            """, throwing = "exception")
    public void logExceptionCatch(JoinPoint joinPoint, Throwable exception) {

        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        log.error("[LOG] Exception tại {}.{} : {}", className, methodName, exception.getMessage());
    }

    @AfterReturning(pointcut = """
            execution(* project_coursemanagement.ptit_cntt1_it211_project_coursemanagement.service.impl..*(..))
            """, returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {

        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        log.info("[LOG] {}.{} completed successfully. Return = {}", className, methodName, result);
    }
}
