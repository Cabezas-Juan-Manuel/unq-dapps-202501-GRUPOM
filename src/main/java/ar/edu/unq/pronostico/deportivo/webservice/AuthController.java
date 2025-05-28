package ar.edu.unq.pronostico.deportivo.webservice;

import ar.edu.unq.pronostico.deportivo.webservice.Dtos.RegisterDto;
import ar.edu.unq.pronostico.deportivo.webservice.Dtos.LoginDto;
import ar.edu.unq.pronostico.deportivo.webservice.Dtos.UserDto;
import ar.edu.unq.pronostico.deportivo.service.UserService;
import ar.edu.unq.pronostico.deportivo.utils.AuthValidator;
import ar.edu.unq.pronostico.deportivo.security.JwtService;
import ar.edu.unq.pronostico.deportivo.utils.UserMapper;
import ar.edu.unq.pronostico.deportivo.model.User;

import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;
    private final UserService userService;

    @Autowired
    public AuthController(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping(value = "login")
    @Transactional
    public ResponseEntity<UserDto> login(@RequestBody LoginDto login){
        User user = userService.getUser(login.getName(), login.getPassword());
        String token = generateTokenFor(user);
        UserDto userDto = UserMapper.convertToDto(user);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(userDto);
    }

    @PostMapping(value = "register")
    @Transactional
    public ResponseEntity<UserDto> register(@RequestBody RegisterDto register){
        AuthValidator.validateRegister(register);

        User newUser = userService.createUser(register.getName(), register.getPassword());
        String token = generateTokenFor(newUser);
        UserDto userDto = UserMapper.convertToDto(newUser);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(userDto);
    }

    private String generateTokenFor(User user) {
        return jwtService.getToken(user);
    }
}
