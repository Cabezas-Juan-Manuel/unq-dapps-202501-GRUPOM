package unit;

import ar.edu.unq.pronostico.deportivo.model.player.Forward;
import ar.edu.unq.pronostico.deportivo.model.player.Player;
import ar.edu.unq.pronostico.deportivo.model.playerFactory.ForwardFactory;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ForwardFactoryTest {
    @Test
    void createPlayer_creates_a_forward(){
        String name = "Krueger";
        String age = "12000";
        String currentTeam = "Mexico";
        String nationality = "Chile";
        Map<String, String> info = new HashMap<>();
        Map<String, String> denfensiveStats = new HashMap<>();
        Map<String, String> offensiveStats = new HashMap<>();
        List<Map<String, String>> infoAndStats = new ArrayList<>();
        infoAndStats.add(info);
        infoAndStats.add(denfensiveStats);
        infoAndStats.add(offensiveStats);
        ForwardFactory factory = new ForwardFactory();
        Player forward = factory.createPlayer(name, age, currentTeam, nationality, infoAndStats);
        assertInstanceOf(Forward.class, forward);
    }
}
