package ar.edu.unq.pronostico.deportivo.model.playerFactory;


import ar.edu.unq.pronostico.deportivo.model.player.Forward;
import ar.edu.unq.pronostico.deportivo.model.player.Player;

import java.util.List;
import java.util.Map;

public class ForwardFactory extends PlayerFactory {

    @Override
    public Player createPlayer(String name, String age, String currentTeam, String nationality, List<Map<String, String>> playerInfoAndStatistics) {
        return new Forward(name, age, currentTeam, nationality, playerInfoAndStatistics.get(1));
    }
}
