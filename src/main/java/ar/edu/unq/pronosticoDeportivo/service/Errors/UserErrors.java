package ar.edu.unq.pronosticoDeportivo.service.Errors;

import lombok.Getter;

@Getter
public enum UserErrors {
    ALREADY_REGISTERED("Someone else has chosen that name"),
    USER_NOT_FOUND("User not found"),
    INVALID_PASSWORD_OR_USERNAME("password or user name invalid");

    private final String message;

    UserErrors(String message) {
        this.message = message;
    }

}