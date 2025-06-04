package unitTest;

import ar.edu.unq.pronostico.deportivo.model.player.Goalkeeper;
import ar.edu.unq.pronostico.deportivo.service.errors.UserErrors;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GoalKeeperTest {

    @Test
    void testGoalKeeperCanCalculatePerformance(){
        Map<String, String> playerStatistics = new HashMap<>();
        Double clear = 3.5;
        Double blocks = 3.5;
        Double fouls = 3.5;
        playerStatistics.put("Clear", clear.toString());
        playerStatistics.put("Blocks", blocks.toString());
        playerStatistics.put("Fouls", fouls.toString());
        Goalkeeper goalkeeper = new Goalkeeper("Walter Benitez", "32", "psv", "Argentino", playerStatistics);
        Double expectedPerformance = clear + blocks - fouls;
        Double actualPerformance = goalkeeper.calculatePerformance();
        assertEquals(expectedPerformance, actualPerformance);
    }

    @Test
    void testGoalKeeperCantCalculatePerformanceIfHeHasNotEnoughStatistics(){
        Map<String, String> playerStatistics = new HashMap<>();
        Double blocks = 3.5;
        Double fouls = 3.5;
        String expectedErrorMessage = UserErrors.MISSING_STATISTICS_ERROR.getMessage();
        playerStatistics.put("Blocks", blocks.toString());
        playerStatistics.put("Fouls", fouls.toString());
        Goalkeeper goalkeeper = new Goalkeeper("Walter Benitez", "32", "psv", "Argentino", playerStatistics);
        RuntimeException exceptionThrown = assertThrows(RuntimeException.class, () -> goalkeeper.calculatePerformance());
        assertEquals(expectedErrorMessage, exceptionThrown.getMessage());
    }

    @Test
    void testGoalKeeperCantCalculatePerformanceIfHeHasNotStatistics(){
        Map<String, String> playerStatistics = null;
        String expectedErrorMessage = UserErrors.MISSING_STATISTICS_ERROR.getMessage();
        Goalkeeper goalkeeper = new Goalkeeper("Walter Benitez", "32", "psv", "Argentino", playerStatistics);
        RuntimeException exceptionThrown = assertThrows(RuntimeException.class, () -> goalkeeper.calculatePerformance());
        assertEquals(expectedErrorMessage, exceptionThrown.getMessage());
    }

}
