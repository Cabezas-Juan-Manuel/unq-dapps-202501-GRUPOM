package unitTest;

import ar.edu.unq.pronostico.deportivo.model.player.MidFielder;
import ar.edu.unq.pronostico.deportivo.errors.Errors;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MidFielderTest {
    @Test
    void testMidFielderCanCalculatePerformance(){
        Map<String, String> playerStatistics = new HashMap<>();
        Double assists = 25.0;
        Double fouls = 3.5;
        Double clear = 5.0;
        Double tackles = 4.5;
        playerStatistics.put("Fouls", fouls.toString());
        playerStatistics.put("Assists", assists.toString());
        playerStatistics.put("Clear", clear.toString());
        playerStatistics.put("Tackles", tackles.toString());
        MidFielder midFielder = new MidFielder("Chapu Brania", "46", "Quilmes", "Argentino", playerStatistics);
        Double expectedPerformance = clear + tackles + (assists / 2) - fouls;
        Double actualPerformance = midFielder.calculatePerformance();
        assertEquals(expectedPerformance, actualPerformance);
    }

    @Test
    void testMidFielderCantCalculatePerformanceIfHeHasNotEnoughStatistics(){
        Map<String, String> playerStatistics = new HashMap<>();
        Double tackles = 3.5;
        Double fouls = 3.5;
        String expectedErrorMessage = Errors.MISSING_STATISTICS_ERROR.getMessage();
        playerStatistics.put("Blocks", tackles.toString());
        playerStatistics.put("Tackles", fouls.toString());
        MidFielder midFielder = new MidFielder("Chapu Brania", "46", "Quilmes", "Argentino", playerStatistics);
        RuntimeException exceptionThrown = assertThrows(RuntimeException.class, () -> midFielder.calculatePerformance());
        assertEquals(expectedErrorMessage, exceptionThrown.getMessage());
    }

    @Test
    void testMidFielderCantCalculatePerformanceIfHeHasNotStatistics(){
        Map<String, String> playerStatistics = null;
        String expectedErrorMessage = Errors.MISSING_STATISTICS_ERROR.getMessage();
        MidFielder midFielder = new MidFielder("Chapu Brania", "46", "Quilmes", "Argentino", playerStatistics);
        RuntimeException exceptionThrown = assertThrows(RuntimeException.class, () -> midFielder.calculatePerformance());
        assertEquals(expectedErrorMessage, exceptionThrown.getMessage());
    }
}
