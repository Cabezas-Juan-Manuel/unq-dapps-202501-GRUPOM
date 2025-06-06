package ar.edu.unq.pronostico.deportivo.model.playerFactory;

import ar.edu.unq.pronostico.deportivo.model.player.Player;
import ar.edu.unq.pronostico.deportivo.errors.Errors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class PlayerFactory {

    public Player generatePlayer(List<Map<String, String>> playerInfoAndStatistics) {
        List<String> playerInfoLabels = List.of("Name:", "Age:", "Current Team:", "Nationality:");
        checkifItHasPlayersInfo(playerInfoAndStatistics, playerInfoLabels);
        List<String> extractedPlayerInfo = extractPlayerInfoLabels(playerInfoLabels, playerInfoAndStatistics.get(0));

        return createPlayerWithPersonalInfoAndStatistics(extractedPlayerInfo.get(0), extractedPlayerInfo.get(1),
                extractedPlayerInfo.get(2), extractedPlayerInfo.get(3), playerInfoAndStatistics);
    }

    private List<String> extractPlayerInfoLabels(List<String> playerInfoLabels, Map<String, String> playerInfo) {
        List<String> extractedPlayerInfo = new ArrayList<>();

        for (String key : playerInfoLabels) {
            extractedPlayerInfo.add(playerInfo.get(key));
        }
        return extractedPlayerInfo;

    }

    private void checkifItHasPlayersInfo(List<Map<String, String>> playerInfoAndStatistics, List<String> playerInfoLabels) {
        if (playerInfoAndStatsAreWrong(playerInfoAndStatistics, playerInfoLabels)){
            throw  new IllegalArgumentException(Errors.PLAYER_INFO_IS_WRONG_OR_NULL.getMessage());
        }
    }

    private static boolean playerInfoAndStatsAreWrong(List<Map<String, String>> playerInfoAndStatistics, List<String> playerInfoLabels) {
        return playerInfoAndStatistics == null  ||
                playerInfoAndStatistics.getFirst().isEmpty() ||
                (!playerInfoAndStatistics.getFirst().keySet().containsAll(playerInfoLabels));
    }

    private Player createPlayerWithPersonalInfoAndStatistics(String name, String age, String currentTeam, String nationality, List<Map<String, String>> playerInfoAndStatistics){
        checkIfItHasPlayersStats(playerInfoAndStatistics);
        return createPlayer(name, age, currentTeam, nationality, playerInfoAndStatistics);
    }

    private void checkIfItHasPlayersStats(List<Map<String, String>> playerInfoAndStatistics) {
        if (playerInfoAndStatistics.size() < 3) {
            throw new IllegalArgumentException(Errors.PLAYER_STATISTICS_ARE_EMPTY.getMessage());
        }
    }

    public abstract Player createPlayer(String name, String age, String currentTeam, String nationality, List<Map<String, String>> playerInfoAndStatistics);

}
