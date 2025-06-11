package ar.edu.unq.pronostico.deportivo.webservice;

import ar.edu.unq.pronostico.deportivo.model.Team;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FootballComparator {

    public static List<Map<String, String>> compareTeams(Team teamOne, Team teamTwo) {
        List<Map<String, String>> comparisonInfo = new ArrayList<>();

        compareDefensiveStats(teamOne, teamTwo, comparisonInfo);

        compareOffensiveStats(teamOne, teamTwo, comparisonInfo);

        return generateComparisonTable(comparisonInfo);
    }

    private static void compareDefensiveStats(Team teamOne, Team teamTwo, List<Map<String, String>> comparisonTable) {
        Map<String, String> shotsReceivedPerGame = Map.of(teamOne.getShotsReceived(), teamTwo.getShotsReceived());
        Map<String, String> interceptionsPerGame = Map.of(teamOne.getinterceptions(), teamTwo.getinterceptions());
        Map<String, String> foulsMadePerGame = Map.of(teamOne.getfoulsMade(), teamTwo.getfoulsMade());

        comparisonTable.add(shotsReceivedPerGame);
        comparisonTable.add(interceptionsPerGame);
        comparisonTable.add(foulsMadePerGame);
    }

    private static void compareOffensiveStats(Team teamOne, Team teamTwo, List<Map<String, String>> comparisonTable) {
        Map<String, String> shotsMadePerGame = Map.of(teamOne.getShotsMade(), teamTwo.getShotsMade());
        Map<String, String> dribblesMadePerGame = new HashMap<>(teamOne.getDribbles(), teamTwo.getDribbles());
        Map<String, String> foulsReceivedPerGame = new HashMap<>(teamOne.getFoulsRecievedPerGame());

        comparisonTable.add(shotsMadePerGame);
        comparisonTable.add(dribblesMadePerGame);
        comparisonTable.add(foulsReceivedPerGame);
    }

    private static List<Map<String, String>> generateComparisonTable(List<Map<String, String>> comparisonInfo) {
    }


}
