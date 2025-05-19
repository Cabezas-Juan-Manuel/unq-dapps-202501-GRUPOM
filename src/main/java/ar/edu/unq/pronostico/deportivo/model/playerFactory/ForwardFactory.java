package ar.edu.unq.pronostico.deportivo.model.playerFactory;


import ar.edu.unq.pronostico.deportivo.model.PlayerH.Defender;
import ar.edu.unq.pronostico.deportivo.model.PlayerH.Forward;
import ar.edu.unq.pronostico.deportivo.model.PlayerH.Player;

import java.util.List;
import java.util.Map;

public class ForwardFactory extends PlayerFactory {

    @Override
    Player createPlayerWithPersonalInfoAndStatistics(String name, String age, String currentTeam, String nationality, List<Map<String, String>> playerInfoAndStatistics) {
        return new Forward(name, age, currentTeam, nationality, playerInfoAndStatistics.get(1));
    }
}
