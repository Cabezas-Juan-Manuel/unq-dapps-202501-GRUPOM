package ar.edu.unq.pronosticodeportivo.utils;

import java.util.regex.Pattern;

import ar.edu.unq.pronosticodeportivo.webservice.Dtos.RegisterDto;

public class AuthValidator {

    private static AuthValidator instance;
    private static final String passwordRegex= "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,}$";


    public static AuthValidator getInstance() {
        if (instance == null){
            instance = new AuthValidator();
        }
        return instance;
    }

    public void ValidateRegister(RegisterDto register) {
        itsAnInValidPassword(register.getPassword());
    }

    private void itsAnInValidPassword(String password) {
        if(!Pattern.matches(passwordRegex, password)) {
            throw new IllegalArgumentException("La contrasenia no es correcta");
        }
    }

}
