package ar.edu.unq.pronostico.deportivo.utils;

import ar.edu.unq.pronostico.deportivo.model.User;
import ar.edu.unq.pronostico.deportivo.webservice.dtos.UserDto;

public class UserMapper {

    private UserMapper() { }

    public static UserDto convertToDto(User newUser) {
        return new UserDto(newUser.getName());
    }
}
