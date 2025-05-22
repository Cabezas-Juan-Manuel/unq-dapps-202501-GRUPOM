package ar.edu.unq.pronostico.deportivo;

import ar.edu.unq.pronostico.deportivo.model.PlayerForTeam;
import ar.edu.unq.pronostico.deportivo.service.UserService;
import ar.edu.unq.pronostico.deportivo.service.integration.dataObject.Match;
import ar.edu.unq.pronostico.deportivo.utils.ApiResponse;
import ar.edu.unq.pronostico.deportivo.webservice.Dtos.RegisterDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PronosticoDeportivoControllerE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserService userService;

    private String token;

    @BeforeAll
    void setUp() {
        String baseUrl = "http://localhost:" + port;

        RegisterDto newUser = new RegisterDto();
        newUser.setName("user");
        newUser.setPassword("password$1");

        ResponseEntity<Void> registerResponse = restTemplate.postForEntity(
                baseUrl + "/auth/register",
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
    void getPlayersFromTeam() {
        String baseUrl = "http://localhost:" + port;
        String teamName = "bayern munich";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.replace("Bearer ", ""));

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<ApiResponse<List<PlayerForTeam>>> response = restTemplate.exchange(
                baseUrl + "/pronosticoDeportivo/team/" + teamName + "/players",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void GetFutureMatchesFromTeam() {
        String baseUrl = "http://localhost:" + port;
        String teamName = "bayern munich";

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
    }
}
