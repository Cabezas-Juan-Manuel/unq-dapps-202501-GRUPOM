package ar.edu.unq.pronostico.deportivo;

import ar.edu.unq.pronostico.deportivo.model.Player;
import ar.edu.unq.pronostico.deportivo.service.UserService;
import ar.edu.unq.pronostico.deportivo.utils.ApiResponse;
import ar.edu.unq.pronostico.deportivo.webservice.Dtos.RegisterDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PronosticoDeportivoControllerE2ETest {

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
    
    @Disabled("No anda el scrapper, se saltea esta prueba")
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

        ResponseEntity<ApiResponse<List<Player>>> response = restTemplate.exchange(
                baseUrl + "/pronosticoDeportivo/team/" + teamName + "/players",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
