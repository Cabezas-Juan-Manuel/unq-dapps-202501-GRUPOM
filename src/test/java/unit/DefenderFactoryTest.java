package unit;

import ar.edu.unq.pronostico.deportivo.model.player.Defender;
import ar.edu.unq.pronostico.deportivo.model.player.Player;
import ar.edu.unq.pronostico.deportivo.model.playerFactory.DefenderFactory;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class DefenderFactoryTest {
    @Test
    void createPlayer_creates_a_defender(){
        String name = "defender";
        String age = "54";
        String currentTeam = "Old";
        String nationality = "Si";
        Map<String, String> info = new HashMap<>();
        Map<String, String> denfensiveStats = new HashMap<>();
        Map<String, String> offensiveStats = new HashMap<>();
        List<Map<String, String>> infoAndStats = new ArrayList<>();
        infoAndStats.add(info);
        infoAndStats.add(denfensiveStats);
        infoAndStats.add(offensiveStats);
        DefenderFactory factory = new DefenderFactory();
        Player goalKeeper = factory.createPlayer(name, age, currentTeam, nationality, infoAndStats);
        assertInstanceOf(Defender.class, goalKeeper);
    }
}
