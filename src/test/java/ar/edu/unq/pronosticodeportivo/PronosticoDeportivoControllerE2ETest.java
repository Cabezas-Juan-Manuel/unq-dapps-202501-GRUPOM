package ar.edu.unq.pronosticodeportivo;

import ar.edu.unq.pronosticodeportivo.model.Player;
import ar.edu.unq.pronosticodeportivo.service.UserService;
import ar.edu.unq.pronosticodeportivo.webservice.Dtos.RegisterDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PronosticoDeportivoControllerE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserService userService;

    @AfterEach
    void cleanUsers() {
        userService.deleteUsers();
    }

    @Test
    void getPlayersFromTeamTest() {
        String baseUrl = "http://localhost:" + port;
        String teamName = "bayern munich";

        RegisterDto newUser = new RegisterDto();
        newUser.setName("user");
        newUser.setPassword("password$1");

        ResponseEntity<Void> registerResponse = restTemplate.postForEntity(
                baseUrl + "/auth/register",
                newUser,
                Void.class
        );

        String token = registerResponse.getHeaders().getFirst("Authorization");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.replace("Bearer ", ""));

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Player[]> response = restTemplate.exchange(
                baseUrl + "/pronosticoDeportivo/team/" + teamName + "/players",
                HttpMethod.GET,
                entity,
                Player[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
