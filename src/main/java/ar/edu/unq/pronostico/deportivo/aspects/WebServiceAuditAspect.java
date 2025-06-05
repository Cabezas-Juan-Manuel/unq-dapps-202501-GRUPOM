package ar.edu.unq.pronostico.deportivo.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class WebServiceAuditAspect {

    private static final Logger logger = LogManager.getLogger();

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void webServiceMethods() {}

    @Around("webServiceMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object[] args = joinPoint.getArgs();
        String user = getUser();
        String method = joinPoint.getSignature().getName();
        String parameters = Arrays.toString(args);

        logger.info("User: '{}' | Method: '{}' | Parameters: {} - Execution started.", user, method, parameters);

        Object result = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - startTime;

        logger.info("User: '{}' | Method: '{}' - Execution finished in {} ms.", user, method, executionTime);

        return result;
    }

    private String getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}

