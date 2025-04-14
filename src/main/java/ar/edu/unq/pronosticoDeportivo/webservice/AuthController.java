package ar.edu.unq.pronosticoDeportivo.webservice;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ar.edu.unq.pronosticoDeportivo.model.User;
import ar.edu.unq.pronosticoDeportivo.security.JwtService;
import ar.edu.unq.pronosticoDeportivo.utils.AuthValidator;
import ar.edu.unq.pronosticoDeportivo.utils.UserMapper;
import ar.edu.unq.pronosticoDeportivo.webservice.Dtos.LoginDto;
import ar.edu.unq.pronosticoDeportivo.webservice.Dtos.RegisterDto;
import ar.edu.unq.pronosticoDeportivo.webservice.Dtos.UserDto;
import ar.edu.unq.pronosticoDeportivo.service.UserService;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserService userService;

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
        AuthValidator.getInstance().ValidateRegister(register);
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
