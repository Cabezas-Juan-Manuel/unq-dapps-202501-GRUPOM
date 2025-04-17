package ar.edu.unq.pronosticodeportivo.webservice;

import ar.edu.unq.pronosticodeportivo.webservice.Dtos.RegisterDto;
import ar.edu.unq.pronosticodeportivo.webservice.Dtos.LoginDto;
import ar.edu.unq.pronosticodeportivo.webservice.Dtos.UserDto;
import ar.edu.unq.pronosticodeportivo.service.UserService;
import ar.edu.unq.pronosticodeportivo.utils.AuthValidator;
import ar.edu.unq.pronosticodeportivo.security.JwtService;
import ar.edu.unq.pronosticodeportivo.utils.UserMapper;
import ar.edu.unq.pronosticodeportivo.model.User;

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
    public ResponseEntity<UserDto> login(@RequestBody LoginDto login){
        User user = userService.getUser(login.getName(), login.getPassword());
        String token = generateTokenFor(user);
        UserDto userDto = UserMapper.convertToDto(user);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        return ResponseEntity.status(HttpStatus.OK).headers(headers).body(userDto);
    }
    @PostMapping(value = "register")
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
