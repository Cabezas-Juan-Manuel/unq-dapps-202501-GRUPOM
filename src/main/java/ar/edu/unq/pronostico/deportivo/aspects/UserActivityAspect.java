package ar.edu.unq.pronostico.deportivo.aspects;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ar.edu.unq.pronostico.deportivo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.time.LocalDateTime;

@Aspect
@Component
public class UserActivityAspect {

    private UserService userService;

    public UserActivityAspect(UserService userService){
        this.userService = userService;
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping) || @annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void getOrPostMappingMethods() {}
    @Before("getOrPostMappingMethods()")
    public void logUserActivity(JoinPoint joinPoint) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null || authentication.getName().equals("anonymousUser")) return;

        HttpServletRequest request = attributes.getRequest();
        String method = request.getMethod();
        String queryParams = request.getQueryString() != null ? "?" + request.getQueryString() : "";
        String fullUrl = request.getRequestURL().toString();
        LocalDateTime timestamp = LocalDateTime.now();

        userService.registerActivity(authentication.getName(), fullUrl, method, queryParams, timestamp);

    }
}

