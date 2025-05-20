package ar.edu.unq.pronostico.deportivo.aspects;

import ar.edu.unq.pronostico.deportivo.security.JwtAuthFilter;
import ar.edu.unq.pronostico.deportivo.security.JwtService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ar.edu.unq.pronostico.deportivo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;

@Aspect
@Component
public class UserActivityAspect {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtAuthFilter jwtAuth;

    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping) || @annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void getOrPostMappingMethods() {}

    @Before("getOrPostMappingMethods()")
    public void logUserActivity(JoinPoint joinPoint) {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null || authentication.getName().equals("anonymousUser")) return;

            HttpServletRequest request = attributes.getRequest();
            String method = request.getMethod();
            String queryParams = request.getQueryString() != null ? "?" + request.getQueryString() : "";
            String fullUrl = request.getRequestURL().toString();
            LocalDateTime timestamp = LocalDateTime.now();
            userService.registerActivity(authentication.getName(), fullUrl, method, queryParams, timestamp);

        } catch (Exception e) {
            System.err.println("Error registrando actividad de usuario: " + e.getMessage());
        }
    }

}

