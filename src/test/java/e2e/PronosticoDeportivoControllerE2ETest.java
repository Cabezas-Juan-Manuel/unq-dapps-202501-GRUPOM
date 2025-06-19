package e2e;

import ar.edu.unq.pronostico.deportivo.PronosticoDeportivoApplication;
import ar.edu.unq.pronostico.deportivo.model.PlayerForTeam;
import ar.edu.unq.pronostico.deportivo.service.UserService;
import ar.edu.unq.pronostico.deportivo.service.integration.ChatService;
import ar.edu.unq.pronostico.deportivo.service.integration.FootballDataService;
import ar.edu.unq.pronostico.deportivo.service.integration.WhoScoredService;
import ar.edu.unq.pronostico.deportivo.service.integration.dataObject.Match;
import ar.edu.unq.pronostico.deportivo.service.integration.dataObject.TeamData;
import ar.edu.unq.pronostico.deportivo.utils.ApiResponse;
import ar.edu.unq.pronostico.deportivo.webservice.dtos.PlayerWithPerformanceScoreDto;
import ar.edu.unq.pronostico.deportivo.webservice.dtos.RegisterDto;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = PronosticoDeportivoApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PronosticoDeportivoControllerE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserService userService;

    private String token;

    @MockitoBean
    private WhoScoredService whoScoredService;

    @MockitoBean
    private FootballDataService footballDataService;

    @Mock
    private ChatService chatService;


    @BeforeAll
    void setUp() {
        String baseUrl = "http://localhost:" + port;

        RegisterDto newUser = new RegisterDto();
        newUser.setName("carlos");
        newUser.setPassword("Password123!");

        ResponseEntity<Void> registerResponse = restTemplate.postForEntity(
                baseUrl + "/auth/login",
                newUser,
                Void.class
        );

        token = registerResponse.getHeaders().getFirst("Authorization");
    }

    @AfterAll
    void cleanUp() {
        userService.deleteUsers();
    }

    @Test
    void testgetPlayersFromTeam() {
        String baseUrl = "http://localhost:" + port;
        String teamName = "bayern munich";

        List<PlayerForTeam> mockPlayers = List.of(
                new PlayerForTeam(),
                new PlayerForTeam()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.replace("Bearer ", ""));

        Mockito.when(whoScoredService.getPlayersFromTeam(teamName)).thenReturn(mockPlayers);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<ApiResponse<List<PlayerForTeam>>> response = restTemplate.exchange(
                baseUrl + "/pronosticoDeportivo/team/" + teamName + "/players",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getData().size());
    }

    @Test
    void testGetFutureMatchesFromTeam() {
        String baseUrl = "http://localhost:" + port;
        String teamName = "bayern munich";
        TeamData milan = new TeamData(3, "Milan", "Mln", "a", "a");
        TeamData bayern = new TeamData(5, "bayern munich", "by", "b", "b");
        List<Match> mockMatches = List.of(
                new Match(1, "3/4/05", "not played", 3, milan, bayern),
                new Match(1, "25/7/25", "not played", 25, bayern, milan)
        );

        Mockito.when(footballDataService.getFuturesMatches(teamName)).thenReturn(mockMatches);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.replace("Bearer ", ""));

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<List<Match>> response = restTemplate.exchange(
                baseUrl + "/pronosticoDeportivo/team/" + teamName + "/matches",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testPlayerPerformance(){
        String baseUrl = "http://localhost:" + port;
        String player = "Neuer";
        List<Map<String, String>> mockPlayerData = new ArrayList<>();

        Map<String, String> mockPlayerInfo = Map.of(
                "Positions:", "Goalkeeper",
                "Name:", "Manuel Neuer",
                "Age:", "39",
                "Current Team:", "Bayern Munich",
                "Nationality:", "German"
        );

        Map<String, String> mockDefensiveStatsInfo = Map.of(
                "Clear", "7.3",
                "Blocks", "7.5",
                "Fouls", "3.5");

        Map<String, String> mockOffensiveStatsInfo = Map.of(
                "goals", "1");
        mockPlayerData.add(mockPlayerInfo);
        mockPlayerData.add(mockOffensiveStatsInfo);
        mockPlayerData.add(mockDefensiveStatsInfo);

        Mockito.when(whoScoredService.getPlayerStatics(player)).thenReturn(mockPlayerData);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.replace("Bearer ", ""));

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<PlayerWithPerformanceScoreDto> response = restTemplate.exchange(
                baseUrl + "/pronosticoDeportivo/playerPerformance?playerName=" + player,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testPredictMatch() {
        String baseUrl = "http://localhost:" + port;
        String team1 = "Milan";
        String team2 = "Napoli";
        String mockPrompt = "Hola, soy un prompt";
        Mockito.when(chatService.getResponse(mockPrompt)).thenReturn(Mono.just("soy un mono string"));
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.replace("Bearer ", ""));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + "/pronosticoDeportivo/predictMatch?team1=" + team1 + "&team2=" + team2,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
