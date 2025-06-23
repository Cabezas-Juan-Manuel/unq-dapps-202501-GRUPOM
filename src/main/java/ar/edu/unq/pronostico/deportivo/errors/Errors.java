package ar.edu.unq.pronostico.deportivo.errors;

import lombok.Getter;

@Getter
public enum Errors {
    ALREADY_REGISTERED("Someone else has chosen that name"),
    USER_NOT_FOUND("User not found"),
    INVALID_PASSWORD_OR_USERNAME("password or user name invalid"),
    PLAYER_INFO_IS_WRONG_OR_NULL("Player info is wrong or null"),
    PLAYER_STATISTICS_ARE_EMPTY("Player statistics are empty"),
    THERES_NO_POSITION_AVAILABLE_FOR_THIS_PLAYER("There is no position available for this player"),
    POSITION_DOES_NOT_MATCH("position does not match regular positions forward, midfielder, defender and goalkeeper"),
    MISSING_STATISTICS_ERROR("Missing required statistics");

    private final String message;

    Errors(String message) {
        this.message = message;
    }

}