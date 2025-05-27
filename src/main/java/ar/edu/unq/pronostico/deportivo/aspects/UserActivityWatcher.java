package ar.edu.unq.pronostico.deportivo.aspects;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ar.edu.unq.pronostico.deportivo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.time.LocalDateTime;


@Component
public class UserActivityWatcher {

    @Autowired
    private  UserService userService;

    public void logUserActivity() {
        try {
            // 1. Dependencia del contexto de Seguridad de Spring
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            // 2. Dependencia del contexto de la Petición Web de Spring
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            // 3. Lógica condicional basada en los contextos anteriores
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

