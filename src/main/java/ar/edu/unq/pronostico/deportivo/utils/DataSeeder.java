package ar.edu.unq.pronostico.deportivo.utils;

import ar.edu.unq.pronostico.deportivo.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserService userService;

    public DataSeeder(UserService usuarioRepository) {
        this.userService = usuarioRepository;
    }

    private String password = "Password123!";
    @Override
    public void run(String... args) throws Exception {
        userService.createUser("carlos", password);
        userService.createUser("anotherCarlos", password);
        userService.createUser("theFinalCarlos", password);
    }
}

