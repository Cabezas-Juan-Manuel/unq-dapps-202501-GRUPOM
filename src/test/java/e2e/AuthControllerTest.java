package e2e;

import ar.edu.unq.pronostico.deportivo.PronosticoDeportivoApplication;
import ar.edu.unq.pronostico.deportivo.repositories.IUserRepository;
import ar.edu.unq.pronostico.deportivo.webservice.dtos.LoginDto;
import ar.edu.unq.pronostico.deportivo.webservice.dtos.RegisterDto;
import ar.edu.unq.pronostico.deportivo.webservice.dtos.UserDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PronosticoDeportivoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Autowired
    IUserRepository userDao;

    @Test
    void test_user_can_register_with_correct_data() {
        RegisterDto registerDto = new RegisterDto("testUser", "thisIsASecure!123Password");

        String registerUrl = "http://localhost:" + port + "/auth/register";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RegisterDto> request = new HttpEntity<>(registerDto, headers);

        ResponseEntity<UserDto> response = restTemplate.postForEntity(registerUrl, request, UserDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getHeaders().get("Authorization"));
        assertNotNull(response.getBody());
        assertEquals("testUser", response.getBody().getName());
    }

    @Test
    void test_user_can_register_with_blank_username_or_password() {
        RegisterDto registerDto = new RegisterDto("testUser", "invalidPassword");

        String registerUrl = "http://localhost:" + port + "/auth/register";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RegisterDto> request = new HttpEntity<>(registerDto, headers);

        ResponseEntity<UserDto> response = restTemplate.postForEntity(registerUrl, request, UserDto.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode()); // TIENE QUE DAR BAD_REQUEST
    }

    @Test
    void test_user_can_login_with_correct_data() {
        LoginDto loginDto = new LoginDto("carlos", "Password123!");

        String registerUrl = "http://localhost:" + port + "/auth/login";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginDto> request = new HttpEntity<>(loginDto, headers);

        ResponseEntity<UserDto> response = restTemplate.postForEntity(registerUrl, request, UserDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getHeaders().get("Authorization"));
        assertNotNull(response.getBody());
        assertEquals("carlos", response.getBody().getName());
    }


    @AfterEach
    void tearDown() {
        userDao.deleteAll(); // Borra todos los usuarios
    }
}
