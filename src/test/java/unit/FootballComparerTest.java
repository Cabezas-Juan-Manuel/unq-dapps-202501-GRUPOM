package unit;

import ar.edu.unq.pronostico.deportivo.model.FootballComparer;
import ar.edu.unq.pronostico.deportivo.model.Team;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class FootballComparerTest {

    @Test
    void testCompareTeams_returnsExpectedTable() {
        String teamOneName = "Bayern";
        String teamTwoName = "Napoli";

        Map<String, String> offensiveStats = Map.of(
                "Shots pg", "15",
                "Dribbles pg", "10",
                "Fouled pg", "8"
        );

        Map<String, String> defensiveStats = Map.of(
                "Shots pg", "9",
                "Interceptions pg", "7",
                "Fouls pg", "5"
        );

        List<Map<String, String>> teamStats = List.of(offensiveStats, defensiveStats);

        Team teamOne = new Team(teamOneName, teamStats);
        Team teamTwo = new Team(teamTwoName, teamStats);

        List<Map<String, String>> result = FootballComparer.compareTeams(teamOne, teamTwo);

        // Verifica que hay 6 filas
        assertEquals(6, result.size());

        // Verifica algunas claves y valores esperados
        Map<String, String> firstRow = result.getFirst();
        assertEquals("Shots Received per game", firstRow.get("Item Name:"));
        assertEquals("9", firstRow.get(teamOneName));
        assertEquals("9", firstRow.get(teamTwoName));

        Map<String, String> lastRow = result.get(5);
        assertEquals("foulsMadePerGame", lastRow.get("Item Name:"));
        assertEquals("8", lastRow.get(teamOneName));
    }

    @Test
    void testCompareTeams_withMissingStats_shouldReturnNullValues() {
        Team teamOne = new Team("River", List.of(Map.of(), Map.of())); // todos los stats vacíos
        Team teamTwo = new Team("Boca", List.of(Map.of(), Map.of()));

        List<Map<String, String>> result = FootballComparer.compareTeams(teamOne, teamTwo);

        assertEquals(6, result.size());

        for (Map<String, String> row : result) {
            assertTrue(row.containsKey("River"));
            assertTrue(row.containsKey("Boca"));
            assertNull(row.get("River"));
            assertNull(row.get("Boca"));
        }
    }
}
