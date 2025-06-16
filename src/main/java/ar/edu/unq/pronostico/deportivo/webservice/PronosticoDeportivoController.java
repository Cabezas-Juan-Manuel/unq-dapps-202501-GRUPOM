package ar.edu.unq.pronostico.deportivo.webservice;

import ar.edu.unq.pronostico.deportivo.model.Team;
import ar.edu.unq.pronostico.deportivo.service.TeamService;
import ar.edu.unq.pronostico.deportivo.service.integration.ChatService;
import ar.edu.unq.pronostico.deportivo.service.integration.FootballDataService;
import ar.edu.unq.pronostico.deportivo.model.PlayerForTeam;
import ar.edu.unq.pronostico.deportivo.model.player.Player;
import ar.edu.unq.pronostico.deportivo.service.PlayerService;
import ar.edu.unq.pronostico.deportivo.service.integration.WhoScoredService;
import ar.edu.unq.pronostico.deportivo.service.integration.dataObject.Match;
import ar.edu.unq.pronostico.deportivo.utils.ApiResponse;
import ar.edu.unq.pronostico.deportivo.webservice.dtos.PlayerWithPerformanceScoreDto;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pronosticoDeportivo")
public class PronosticoDeportivoController {

    private final WhoScoredService whoScoredService;
    private final FootballDataService footballDataService;
    private final PlayerService playerService;
    private final ChatService chatService;
    private final MeterRegistry meterRegistry;
    private final TeamService teamService;

    public PronosticoDeportivoController(WhoScoredService whoScoredService, FootballDataService footballDataService, PlayerService playerService,
                                         ChatService chatService, MeterRegistry meterRegistry, TeamService teamService) {
        this.whoScoredService = whoScoredService;
        this.footballDataService = footballDataService;
        this.playerService = playerService;
        this.chatService = chatService;
        this.meterRegistry = meterRegistry;
        this.teamService = teamService;
    }

    @Operation(summary = "gets team players", description = "returns a list of players of the team with data about them, name, matches played, goals" +
            " assists and rating")
    @Parameter(example = "Bayern Munich")
    @GetMapping("/team/{teamName}/players")
    @Transactional
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

    @Operation(summary = "gets a list of the teams next matches", description = "returns a list of upcoming matches for a team with information about the match" +
            " as the rivals name, if the team plays home or away and the date of the match")
    @Parameter(example = "Bayern Munich")
    @GetMapping("/team/{teamName}/matches")
    @Transactional
    public ResponseEntity<List<Match>> getFuturesMatches(@PathVariable String teamName) {
        Counter.builder("pronostico.deportivo.future.matches.requests")
                .tag("team", teamName)
                .description("Counts all requests made to the getFutureMatches endpoint, categorized by team.")
                .register(meterRegistry)
                .increment();

        return ResponseEntity.ok(footballDataService.getFuturesMatches(teamName));
    }


    @Operation(summary = "get player performance", description = "returns the performance of a player as a number calculated with statistics related to the " +
            " players position in the filed and information of the player, name, nationality, age and team")
    @Parameter(example = "Robert Lewandowski")
    @GetMapping("playerPerformance")
    @Transactional
    public ResponseEntity<PlayerWithPerformanceScoreDto> playerPerformance(@RequestParam String playerName) {
        List<Map<String, String>> playerData = whoScoredService.getPlayerStatics(playerName);
        Player player = playerService.makePlayerFromData(playerData);
        Double performanceScore = playerService.getPerformanceForPlayer(player);
        PlayerWithPerformanceScoreDto playerDto = new PlayerWithPerformanceScoreDto(player.getName(), player.getAge(), player.getNationality(), player
                .getTeam(), performanceScore.toString());
        return ResponseEntity.status(HttpStatus.OK).body(playerDto);
    }

    @Operation(summary = "predicts a match between two given teams", description = "asks an ia about the result of a match with two given teams and returns the result")
    @Parameter(example = "Bayern Munich")
    @Parameter(example = "Napoli")
    @GetMapping("predictMatch")
    @Transactional
    public Mono<ResponseEntity<String>> predictMatch(
            @RequestParam String team1,
            @RequestParam String team2
    ) {
        String prompt = String.format("¿Quién ganará el partido entre %s y %s? Da una respuesta super corta en porcentajes sino me enojo.", team1, team2);
        Mono<String> chatResponse = chatService.getResponse(prompt);
        return chatResponse.map(ResponseEntity::ok).onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar predicción"));
    }

    @Operation(summary = "compares two given teams", description = "generates a table comparing offensive and defensive statistics of the two given teams")
    @Parameter(example = "Bayern Munich")
    @Parameter(example = "Napoli")
    @GetMapping("compareTeams")
    @Transactional
    public ResponseEntity<List<Map<String, String>>> compareTeams(
            @RequestParam String teamOneName,
            @RequestParam String teamTwoName
    ) {
        Team teamOne = generateTeam(teamOneName);
        Team teamTwo = generateTeam(teamTwoName);
        List<Map<String, String>> comparisonTable = teamService.compareTeams(teamOne, teamTwo);
        return ResponseEntity.status(HttpStatus.OK).body(comparisonTable);
    }


    @Operation(summary = "A performance calculation of the team", description = " From a given team, " +
            "the AI is consulted to perform a calculation of its choice to obtain the team's performance.")
    @Parameter(example = "Bayern Munich")
    @GetMapping("advanceTeamPerformance")
    @Transactional
    public Mono<ResponseEntity<String>> advanceTeamPerformance(
            @RequestParam String teamName
    ) {
        Team team = generateTeam(teamName);
        String prompt = String.format("Generame una metrica compleja para medir el exito del equipo", teamName, "en base a los siguientes datos, disparos al arco:",  team.getShotsMade(), "Dribbles:",
                                        team.getDribbles(), "disparos al arco recibidos:", team.getShotsReceived(), "fouls realizadas:", team.getfoulsMade());
        Mono<String> chatResponse = chatService.getResponse(prompt);
        return chatResponse.map(ResponseEntity::ok).onErrorReturn(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar predicción"));
    }

    private Team generateTeam(String teamName){
        List<Map<String, String>> teamData = whoScoredService.getStatisticsForTeam(teamName);
        return teamService.createTeamFromData(teamName, teamData);
    }
}
