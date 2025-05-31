package unitTest;


import ar.edu.unq.pronostico.deportivo.model.player.Goalkeeper;
import ar.edu.unq.pronostico.deportivo.model.player.Player;
import ar.edu.unq.pronostico.deportivo.model.playerFactory.GoalkeeperFactory;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;


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
