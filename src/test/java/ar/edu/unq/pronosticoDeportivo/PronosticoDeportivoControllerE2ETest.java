package ar.edu.unq.pronosticoDeportivo;

import ar.edu.unq.pronosticoDeportivo.model.Player;
import ar.edu.unq.pronosticoDeportivo.service.integration.WhoScoredService;
import ar.edu.unq.pronosticoDeportivo.utils.JsonParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PronosticoDeportivoControllerE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void getPlayersTest() {
        String baseUrl = "http://localhost:" + port;
        String teamName = "bayern munich";

        TestRestTemplate authenticatedRestTemplate = restTemplate.withBasicAuth("user", "1234");

        ResponseEntity<Player[]> response = authenticatedRestTemplate.getForEntity(
                baseUrl + "/pronosticoDeportivo/team/" + teamName + "/players",
                Player[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
