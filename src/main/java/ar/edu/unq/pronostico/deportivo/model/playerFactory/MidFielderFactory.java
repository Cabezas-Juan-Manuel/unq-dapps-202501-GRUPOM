package ar.edu.unq.pronostico.deportivo.model.playerFactory;

import ar.edu.unq.pronostico.deportivo.model.player.MidFielder;
import ar.edu.unq.pronostico.deportivo.model.player.Player;

import java.util.List;
import java.util.Map;

public class MidFielderFactory extends PlayerFactory {

    @Override
    public Player createPlayer(String name, String age, String currentTeam, String nationality, List<Map<String, String>> playerInfoAndStatistics) {
        Map<String, String> playerPerfomance = playerInfoAndStatistics.get(1);
        playerPerfomance.putAll(playerInfoAndStatistics.get(2));
        return new MidFielder(name, age, currentTeam, nationality, playerPerfomance);
    }
}
