package ar.edu.unq.pronostico.deportivo.model.playerFactory;


import ar.edu.unq.pronostico.deportivo.model.Player.Defender;
import ar.edu.unq.pronostico.deportivo.model.Player.Player;

import java.util.List;
import java.util.Map;

public class DefenderFactory extends PlayerFactory {

    @Override
    Player createPlayerWithPersonalInfoAndStatistics(String name, String age, String currentTeam, String nationality, List<Map<String, String>> playerInfoAndStatistics) {
        return new Defender(name, age, currentTeam, nationality, playerInfoAndStatistics.get(2));
    }
}
