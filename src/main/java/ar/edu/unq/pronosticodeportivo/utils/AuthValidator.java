package ar.edu.unq.pronosticodeportivo.utils;

import java.util.regex.Pattern;

import ar.edu.unq.pronosticodeportivo.webservice.Dtos.RegisterDto;

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
