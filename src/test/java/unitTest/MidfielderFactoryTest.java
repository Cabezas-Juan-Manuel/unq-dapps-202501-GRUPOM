package unitTest;

import ar.edu.unq.pronostico.deportivo.model.Player.Goalkeeper;
import ar.edu.unq.pronostico.deportivo.model.Player.MidFielder;
import ar.edu.unq.pronostico.deportivo.model.Player.Player;
import ar.edu.unq.pronostico.deportivo.model.playerFactory.GoalkeeperFactory;
import ar.edu.unq.pronostico.deportivo.model.playerFactory.MidFielderFactory;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class MidfielderFactoryTest {
    @Test
    void createPlayer_creates_a_midfielder(){
        String name = "carlos";
        String age = "500";
        String currentTeam = "Alaves";
        String nationality = "Vietnamita";
        Map<String, String> info = new HashMap<>();
        Map<String, String> denfensiveStats = new HashMap<>();
        Map<String, String> offensiveStats = new HashMap<>();
        List<Map<String, String>> infoAndStats = new ArrayList<>();
        infoAndStats.add(info);
        infoAndStats.add(denfensiveStats);
        infoAndStats.add(offensiveStats);
        MidFielderFactory factory = new MidFielderFactory();
        Player midfielder = factory.createPlayer(name, age, currentTeam, nationality, infoAndStats);
        assertInstanceOf(MidFielder.class, midfielder);
    }
}
