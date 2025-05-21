package ar.edu.unq.pronostico.deportivo.webservice;

import ar.edu.unq.pronostico.deportivo.service.integration.FootballDataService;
import ar.edu.unq.pronostico.deportivo.model.PlayerForTeam;
import ar.edu.unq.pronostico.deportivo.model.Player.Player;
import ar.edu.unq.pronostico.deportivo.service.PlayerService;
import ar.edu.unq.pronostico.deportivo.service.integration.WhoScoredService;

import ar.edu.unq.pronostico.deportivo.service.integration.dataObject.Match;
import ar.edu.unq.pronostico.deportivo.utils.ApiResponse;
import ar.edu.unq.pronostico.deportivo.webservice.Dtos.PlayerWithPerformanceScoreDto;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pronosticoDeportivo")
public class PronosticoDeportivoController {

    private final WhoScoredService whoScoredService;
    private final FootballDataService footballDataService;
    private final PlayerService playerService;

    public PronosticoDeportivoController(WhoScoredService whoScoredService, FootballDataService footballDataService, PlayerService playerService) {
        this.whoScoredService = whoScoredService;
        this.footballDataService = footballDataService;
        this.playerService = playerService;
    }


    @GetMapping("/team/{teamName}/players")
    public ResponseEntity<ApiResponse<List<PlayerForTeam>>> getPlayersFromTeam(@PathVariable String teamName) {
        try {
            List<PlayerForTeam> players = whoScoredService.getPlayersFromTeam(teamName);

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

    @GetMapping("/team/{teamName}/matches")
    public ResponseEntity<List<Match>> getFuturesMatches(@PathVariable String teamName) {
        return ResponseEntity.ok(footballDataService.getFuturesMatches(teamName));
    }

    @GetMapping("playerPerformance")
    public ResponseEntity<PlayerWithPerformanceScoreDto> playerPerformance(@RequestParam String playerName) {
        List<Map<String, String>> playerData = whoScoredService.getPlayerStatics(playerName);
        Player player = playerService.makePlayerFromData(playerData);
        Double performanceScore = playerService.getPerformanceForPlayer(player);
        PlayerWithPerformanceScoreDto playerDto = new PlayerWithPerformanceScoreDto(player.getName(), player.getAge(), player.getNationality(), player
                .getTeam(), performanceScore.toString());
        return ResponseEntity.status(HttpStatus.OK).body(playerDto);
    }
}
