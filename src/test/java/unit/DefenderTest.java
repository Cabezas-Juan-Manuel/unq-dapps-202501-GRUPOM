package unit;

import ar.edu.unq.pronostico.deportivo.model.player.Defender;
import ar.edu.unq.pronostico.deportivo.errors.Errors;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DefenderTest {
    @Test
    void testDefenderCanCalculatePerformance(){
        Map<String, String> playerStatistics = new HashMap<>();
        Double clear = 3.5;
        Double blocks = 3.5;
        Double fouls = 3.5;
        Double inter = 2.5;
        playerStatistics.put("Clear", clear.toString());
        playerStatistics.put("Blocks", blocks.toString());
        playerStatistics.put("Fouls", fouls.toString());
        playerStatistics.put("Inter", inter.toString());
        Defender defender = new Defender("Romero", "27", "tottenham", "Argentino", playerStatistics);
        Double expectedPerformance = clear + inter + blocks - fouls;
        Double actualPerformance = defender.calculatePerformance();
        assertEquals(expectedPerformance, actualPerformance);
    }

    @Test
    void testDefenderCantCalculatePerformanceIfHeHasNotEnoughStatistics(){
        Map<String, String> playerStatistics = new HashMap<>();
        Double blocks = 3.5;
        Double fouls = 3.5;
        String expectedErrorMessage = Errors.MISSING_STATISTICS_ERROR.getMessage();
        playerStatistics.put("Blocks", blocks.toString());
        playerStatistics.put("Fouls", fouls.toString());
        Defender defender = new Defender("Romero", "27", "tottenham", "Argentino", playerStatistics);
        RuntimeException exceptionThrown = assertThrows(RuntimeException.class, () -> defender.calculatePerformance());
        assertEquals(expectedErrorMessage, exceptionThrown.getMessage());
    }

    @Test
    void testDefenderCantCalculatePerformanceIfHeHasNotStatistics(){
        Map<String, String> playerStatistics = null;
        String expectedErrorMessage = Errors.MISSING_STATISTICS_ERROR.getMessage();
        Defender defender = new Defender("Romero", "27", "tottenham", "Argentino", playerStatistics);
        RuntimeException exceptionThrown = assertThrows(RuntimeException.class, () -> defender.calculatePerformance());
        assertEquals(expectedErrorMessage, exceptionThrown.getMessage());
    }
}
