package ar.edu.unq.pronosticodeportivo.webservice;

import ar.edu.unq.pronosticodeportivo.service.integration.WhoScoredService;
import ar.edu.unq.pronosticodeportivo.utils.ApiError;
import ar.edu.unq.pronosticodeportivo.model.Player;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RestController
@RequestMapping("/pronosticoDeportivo")
public class PronosticoDeportivoController {

    @GetMapping("/team/{teamName}/players")
    public ResponseEntity<?> getPlayersFromTeam(@PathVariable String teamName) {
        try {
            List<Player> players = WhoScoredService.getPlayersFromTeam(teamName);

            if (players.isEmpty()) {
                ApiError error = new ApiError(HttpStatus.NOT_FOUND.value(), "No players found for team: " + teamName);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            return ResponseEntity.ok(players);

        } catch (IllegalArgumentException e) {
            ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An error occurred while retrieving players");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
