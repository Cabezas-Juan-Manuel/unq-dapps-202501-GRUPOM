package ar.edu.unq.pronostico.deportivo.model;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class FootballComparer {

    public static List<Map<String, String>> compareTeams(Team teamOne, Team teamTwo) {
        List<Map<String, String>> comparisonTable = new ArrayList<>();

        String itemToCompere = "Item Name:";

        compareDefensiveStats(teamOne, teamTwo, comparisonTable, itemToCompere);

        compareOffensiveStats(teamOne, teamTwo, comparisonTable, itemToCompere);

        return comparisonTable;
    }

    private static void compareDefensiveStats(Team teamOne, Team teamTwo, List<Map<String, String>> comparisonTable, String itemToCompere) {
        String shotsReceivedPerGameName = "Shots Received per game";
        String interceptionsPerGameName = "Interceptions per game";
        String foulsMadePerGameName = "fouls Made Per Game";

        Map<String, String> shotsReceivedPerGame = new LinkedHashMap<>();
        shotsReceivedPerGame.put(itemToCompere, shotsReceivedPerGameName);
        shotsReceivedPerGame.put(teamOne.getName(), teamOne.getShotsReceived());
        shotsReceivedPerGame.put(teamTwo.getName(), teamTwo.getShotsReceived());

        Map<String, String> interceptionsPerGame = new LinkedHashMap<>();
        interceptionsPerGame.put(itemToCompere, interceptionsPerGameName);
        interceptionsPerGame.put(teamOne.getName(), teamOne.getinterceptions());
        interceptionsPerGame.put(teamTwo.getName(), teamTwo.getinterceptions());

        Map<String, String> foulsMadePerGame = new LinkedHashMap<>();
        foulsMadePerGame.put(itemToCompere, foulsMadePerGameName);
        foulsMadePerGame.put(teamOne.getName(), teamOne.getfoulsMade());
        foulsMadePerGame.put(teamTwo.getName(), teamTwo.getfoulsMade());

        comparisonTable.add(shotsReceivedPerGame);
        comparisonTable.add(interceptionsPerGame);
        comparisonTable.add(foulsMadePerGame);
    }

    private static void compareOffensiveStats(Team teamOne, Team teamTwo, List<Map<String, String>> comparisonTable, String nameForComparisions) {
        String shotsMadePerGameName = "Shots Received per game";
        String dribblesMadePerGameName = "Interceptions per game";
        String foulsReceivedPerGameName = "foulsMadePerGame";

        Map<String, String> shotsMadePerGame = new LinkedHashMap<>();
        shotsMadePerGame.put(nameForComparisions, shotsMadePerGameName);
        shotsMadePerGame.put(teamOne.getName(), teamOne.getShotsMade());
        shotsMadePerGame.put(teamTwo.getName(), teamTwo.getShotsMade());

        Map<String, String> dribblesMadePerGame = new LinkedHashMap<>();
        dribblesMadePerGame.put(nameForComparisions, dribblesMadePerGameName);
        dribblesMadePerGame.put(teamOne.getName(), teamOne.getDribbles());
        dribblesMadePerGame.put(teamTwo.getName(), teamTwo.getDribbles());

        Map<String, String> foulsReceivedPerGame = new LinkedHashMap<>();
        foulsReceivedPerGame.put(nameForComparisions, foulsReceivedPerGameName);
        foulsReceivedPerGame.put(teamOne.getName(), teamOne.getFoulsRecievedPerGame());
        foulsReceivedPerGame.put(teamTwo.getName(), teamTwo.getFoulsRecievedPerGame());

        comparisonTable.add(shotsMadePerGame);
        comparisonTable.add(dribblesMadePerGame);
        comparisonTable.add(foulsReceivedPerGame);
    }

}
