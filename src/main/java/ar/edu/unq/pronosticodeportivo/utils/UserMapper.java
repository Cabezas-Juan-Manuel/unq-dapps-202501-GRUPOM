package ar.edu.unq.pronosticodeportivo.utils;

import ar.edu.unq.pronosticodeportivo.model.User;
import ar.edu.unq.pronosticodeportivo.webservice.Dtos.UserDto;

public class UserMapper {

    private UserMapper() { }

    public static UserDto convertToDto(User newUser) {   
        return new UserDto(newUser.getName());
    }
}
