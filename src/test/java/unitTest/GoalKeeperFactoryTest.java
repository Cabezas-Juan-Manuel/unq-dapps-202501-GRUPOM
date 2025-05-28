package unitTest;


import ar.edu.unq.pronostico.deportivo.model.Player.Goalkeeper;
import ar.edu.unq.pronostico.deportivo.model.Player.Player;
import ar.edu.unq.pronostico.deportivo.model.playerFactory.GoalkeeperFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension; // Necesario para @ExtendWith

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;


@ExtendWith(MockitoExtension.class) // Facilita el uso de anotaciones de Mockito
public class GoalKeeperFactoryTest {
    @Test
    void createPlayer_creates_a_goalkeeper(){
        String name = "Benji";
        String age = "32";
        String currentTeam = "Quilmes";
        String nationality = "Japones";
        Map<String, String> info = new HashMap<>();
        Map<String, String> denfensiveStats = new HashMap<>();
        Map<String, String> offensiveStats = new HashMap<>();
        List<Map<String, String>> infoAndStats = new ArrayList<>();
        infoAndStats.add(info);
        infoAndStats.add(denfensiveStats);
        infoAndStats.add(offensiveStats);
        GoalkeeperFactory factory = new GoalkeeperFactory();
        Player goalKeeper = factory.createPlayer(name, age, currentTeam, nationality, infoAndStats);
        assertInstanceOf(Goalkeeper.class, goalKeeper);
    }
}
