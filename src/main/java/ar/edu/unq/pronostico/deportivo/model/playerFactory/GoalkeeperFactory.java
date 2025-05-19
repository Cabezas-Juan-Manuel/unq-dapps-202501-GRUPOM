package ar.edu.unq.pronostico.deportivo.model.playerFactory;

import ar.edu.unq.pronostico.deportivo.model.PlayerH.Goalkeeper;
import ar.edu.unq.pronostico.deportivo.model.PlayerH.MidFielder;
import ar.edu.unq.pronostico.deportivo.model.PlayerH.Player;

import java.util.List;
import java.util.Map;

public class GoalkeeperFactory extends PlayerFactory {

    @Override
    Player createPlayerWithPersonalInfoAndStatistics(String name, String age, String currentTeam, String nationality, List<Map<String, String>> playerInfoAndStatistics) {
        return new Goalkeeper(name, age, currentTeam, nationality, playerInfoAndStatistics.get(2));
    }
}
