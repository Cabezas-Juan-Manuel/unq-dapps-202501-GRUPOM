package unitTest;

import ar.edu.unq.pronostico.deportivo.model.playerFactory.PlayerGenerator;
import ar.edu.unq.pronostico.deportivo.model.playerFactory.PlayerFactory;
import ar.edu.unq.pronostico.deportivo.model.Player.Player;
import ar.edu.unq.pronostico.deportivo.service.Errors.UserErrors;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PlayerGeneratorTest {
    private PlayerGenerator generator;
    private PlayerFactory mockedFactory;

    @Test
    void generatePlayerWithData_callsFactory() {
        generator = new PlayerGenerator();
        mockedFactory = mock(PlayerFactory.class);
        generator.getFactoryMap().put("Forward", mockedFactory);
        List<Map<String, String>> positionsAndInfo = new ArrayList<>();
        Map<String, String> positions = new HashMap<>();
        positions.put("Positions:", "Forward");
        positionsAndInfo.add(positions);

        Player mockedPlayer = mock(Player.class);
        when(mockedFactory.generatePlayer(positionsAndInfo)).thenReturn(mockedPlayer);

        Player generatedPlayer = generator.generatePlayerWithData(positionsAndInfo);

        verify(mockedFactory, times(1)).generatePlayer(positionsAndInfo);
    }

    @Test
    void generatePlayerWithoutPositionData_throwsError() {
        generator = new PlayerGenerator();
        mockedFactory = mock(PlayerFactory.class);
        generator.getFactoryMap().put("Forward", mockedFactory);
        List<Map<String, String>> positionsAndInfo = new ArrayList<>();
        Map<String, String> positions = new HashMap<>();
        positions.put("NotPosition:", "SomeValue");
        positionsAndInfo.add(positions);

        String expectedError = UserErrors.THERES_NO_POSITION_AVAILABLE_FOR_THIS_PLAYER.getMessage();
        IllegalArgumentException exceptionThrown = assertThrows(IllegalArgumentException.class,
                () -> generator.generatePlayerWithData(positionsAndInfo));

        assertEquals(expectedError, exceptionThrown.getMessage());
    }

    @Test
    void generatePlayerWithWrongPositionData_throwsError() {
        generator = new PlayerGenerator();
        mockedFactory = mock(PlayerFactory.class);
        generator.getFactoryMap().put("Forward", mockedFactory);
        List<Map<String, String>> positionsAndInfo = new ArrayList<>();
        Map<String, String> positions = new HashMap<>();
        positions.put("Positions:", "WrongPosition");
        positionsAndInfo.add(positions);

        String expectedError = UserErrors.POSITION_DOES_NOT_MATCH.getMessage();
        NullPointerException exceptionThrown = assertThrows(NullPointerException.class,
                () -> generator.generatePlayerWithData(positionsAndInfo));

        assertEquals(expectedError, exceptionThrown.getMessage());
    }
}

