package ar.edu.unq.pronostico.deportivo.model.playerFactory;

import ar.edu.unq.pronostico.deportivo.model.PlayerH.Player;

import java.util.List;
import java.util.Map;

public abstract class PlayerFactory {

    public Player generatePlayer(List<Map<String, String>> playerInfoAndStatistics) {
        Map<String, String> playerInfo = playerInfoAndStatistics.get(0);
        String name = playerInfo.get("Name:");
        String age = playerInfo.get("Age:");
        String currentTeam = playerInfo.get("Current Team:");
        String nationality = playerInfo.get("Nationality:");
        return createPlayerWithPersonalInfoAndStatistics(name, age, currentTeam, nationality, playerInfoAndStatistics);
    }

    abstract Player createPlayerWithPersonalInfoAndStatistics(String name, String age, String currentTeam, String nationality, List<Map<String, String>> playerInfoAndStatistics);


}
