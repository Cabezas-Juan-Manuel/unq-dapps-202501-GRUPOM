package ar.edu.unq.pronosticoDeportivo.utils;

import ar.edu.unq.pronosticoDeportivo.model.User;
import ar.edu.unq.pronosticoDeportivo.webservice.Dtos.UserDto;

public class UserMapper {
    public static UserDto convertToDto(User newUser) {   
        return new UserDto(newUser.getName());
    }
}
