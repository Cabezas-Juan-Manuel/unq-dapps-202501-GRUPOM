package ar.edu.unq.pronostico.deportivo.webservice;

import ar.edu.unq.pronostico.deportivo.model.Player;
import ar.edu.unq.pronostico.deportivo.service.integration.WhoScoredService;

import ar.edu.unq.pronostico.deportivo.utils.ApiResponse;
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

    private final WhoScoredService whoScoredService;

    public PronosticoDeportivoController(WhoScoredService whoScoredService) {
        this.whoScoredService = whoScoredService;
    }


    @GetMapping("/team/{teamName}/players")
    public ResponseEntity<ApiResponse<List<Player>>> getPlayersFromTeam(@PathVariable String teamName) {
        try {
            List<Player> players = whoScoredService.getPlayersFromTeam(teamName);

            if (players.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(HttpStatus.NOT_FOUND.name(), "Team players not found", null, null));
            }
            return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK.name(), "Data retrieved successfully", players, null));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(HttpStatus.BAD_REQUEST.name(), "Bad request", null, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.name(), "Internal server error", null, null));
        }
    }
}
