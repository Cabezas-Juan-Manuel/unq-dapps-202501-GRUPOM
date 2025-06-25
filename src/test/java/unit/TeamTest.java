package unit;

import ar.edu.unq.pronostico.deportivo.model.Team;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TeamTest {
    @Test
    void testConstructorAndGetters() {
        Team team = getTeam();

        assertEquals("Bayern Munich", team.getName());
        assertEquals("15", team.getShotsMade());
        assertEquals("10", team.getDribbles());
        assertEquals("8", team.getFoulsRecievedPerGame());
        assertEquals("9", team.getShotsReceived());
        assertEquals("7", team.getinterceptions());
        assertEquals("5", team.getfoulsMade());
    }

    private static Team getTeam() {
        String teamName = "Bayern Munich";

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

        return new Team(teamName, teamStats);
    }

    @Test
    void testConstructorWithInsufficientStats_shouldThrow() {
        List<Map<String, String>> teamStats = List.of(
                Map.of("Shots pg", "15") // solo uno (ofensivo o defensivo)
        );

        Exception exception = assertThrows(IndexOutOfBoundsException.class, () -> new Team("Napoli", teamStats));

        assertTrue(exception.getMessage().contains("Index"));
    }

    @Test
    void testNullValuesInMaps_shouldReturnNull() {
        Map<String, String> offensiveStats = Map.of(); // vacío
        Map<String, String> defensiveStats = Map.of(); // vacío

        List<Map<String, String>> teamStats = List.of(offensiveStats, defensiveStats);

        Team team = new Team("River Plate", teamStats);

        assertNull(team.getShotsMade());
        assertNull(team.getDribbles());
        assertNull(team.getFoulsRecievedPerGame());
        assertNull(team.getShotsReceived());
        assertNull(team.getinterceptions());
        assertNull(team.getfoulsMade());
    }
}
