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

    @Override
    public void run(String... args) throws Exception {
        userService.createUser("carlos", "Password123!");
        userService.createUser("anotherCarlos", "Password123!");
        userService.createUser("theFinalCarlos", "Password123!");
    }
}

