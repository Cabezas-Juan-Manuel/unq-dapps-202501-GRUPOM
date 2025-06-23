package unit;

import ar.edu.unq.pronostico.deportivo.model.player.Forward;
import ar.edu.unq.pronostico.deportivo.errors.Errors;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ForwardTest {

    @Test
    void testForwardCanCalculatePerformance(){
        Map<String, String> playerStatistics = new HashMap<>();
        Double goals = 40.00;
        Double assists =  25.00;
        playerStatistics.put("Goals", goals.toString());
        playerStatistics.put("Assists", assists.toString());
        Forward forward = new Forward("El diegote", "10", "Napoli", "Argentino", playerStatistics);
        Double expectedPerformance = (goals / 2) + (assists / 2);
        Double actualPerformance = forward.calculatePerformance();
        assertEquals(expectedPerformance, actualPerformance);
    }

    @Test
    void testForwardCantCalculatePerformanceIfHeHasNotEnoughStatistics(){
        Map<String, String> playerStatistics = new HashMap<>();
        Double blocks = 3.5;
        Double fouls = 3.5;
        String expectedErrorMessage = Errors.MISSING_STATISTICS_ERROR.getMessage();
        playerStatistics.put("Blocks", blocks.toString());
        playerStatistics.put("Fouls", fouls.toString());
        Forward forward = new Forward("El diegote", "10", "Napoli", "Argentino", playerStatistics);
        RuntimeException exceptionThrown = assertThrows(RuntimeException.class, () -> forward.calculatePerformance());
        assertEquals(expectedErrorMessage, exceptionThrown.getMessage());
    }

    @Test
    void testForwardCantCalculatePerformanceIfHeHasNotStatistics(){
        Map<String, String> playerStatistics = null;
        String expectedErrorMessage = Errors.MISSING_STATISTICS_ERROR.getMessage();
        Forward forward = new Forward("El diegote", "10", "Napoli", "Argentino", playerStatistics);
        RuntimeException exceptionThrown = assertThrows(RuntimeException.class, () -> forward.calculatePerformance());
        assertEquals(expectedErrorMessage, exceptionThrown.getMessage());
    }
}
