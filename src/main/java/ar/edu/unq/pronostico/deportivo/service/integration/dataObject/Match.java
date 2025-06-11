package ar.edu.unq.pronostico.deportivo.service.integration.dataObject;

public record Match(
        int id,
        String utcDate,
        String status,
        int matchday,
        TeamData homeTeam,
        TeamData awayTeam
) {
}
