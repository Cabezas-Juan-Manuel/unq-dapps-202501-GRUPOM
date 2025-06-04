package ar.edu.unq.pronostico.deportivo.utils;

import java.util.regex.Pattern;

import ar.edu.unq.pronostico.deportivo.webservice.dtos.RegisterDto;

public class AuthValidator {

    private static final String PASSWORDREGEX = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,}$";

    private AuthValidator() { }

    public static void validateRegister(RegisterDto register) {
        itsAnInValidPassword(register.getPassword());
    }

    private static void itsAnInValidPassword(String password) {
        if(!Pattern.matches(PASSWORDREGEX, password)) {
            throw new IllegalArgumentException("La contrasenia no es correcta");
        }
    }

}
