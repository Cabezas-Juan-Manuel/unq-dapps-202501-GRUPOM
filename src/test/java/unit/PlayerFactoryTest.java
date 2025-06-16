package unit;

import ar.edu.unq.pronostico.deportivo.model.player.Player;
import ar.edu.unq.pronostico.deportivo.model.playerFactory.PlayerFactory;
import ar.edu.unq.pronostico.deportivo.errors.Errors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PlayerFactoryTest {

    @Mock
    private Player mockPlayer;

    @InjectMocks
    private PlayerFactory playerFactory = new PlayerFactory() {
        @Override
        public Player createPlayer(String name, String age, String currentTeam, String nationality, List<Map<String, String>> playerInfoAndStatistics) {
            return mockPlayer;
        }
    };

    private List<Map<String, String>> validPlayerInfo;
    private List<Map<String, String>> invalidPlayerInfo;

    @BeforeEach
    void setUp() {
        validPlayerInfo = List.of(
                Map.of("Name:", "Lionel Messi", "Age:", "37", "Current Team:", "Inter Miami", "Nationality:", "Argentinian"),
                Map.of("Goals:", "700", "Assists:", "300"),
                Map.of("Matches:", "1000")
        );

        invalidPlayerInfo = List.of(
                Map.of("Name:", "Cristiano Ronaldo", "Age:", "39")
        );
    }

    @Test
    void testGeneratePlayerWithValidData() {
        Player player = playerFactory.generatePlayer(validPlayerInfo);
        assertNotNull(player);
    }

    @Test
    void testGeneratePlayerWithInvalidDataThrowsException() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            playerFactory.generatePlayer(invalidPlayerInfo);
        });
        assertEquals(Errors.PLAYER_INFO_IS_WRONG_OR_NULL.getMessage(), exception.getMessage());
    }

    @Test
    void testCheckIfItHasPlayersStatsThrowsExceptionIfNotEnoughStats() {
        List<Map<String, String>> playerInfoWithoutStats = List.of(validPlayerInfo.get(0));
        Exception exception = assertThrows(RuntimeException.class, () -> {
            playerFactory.generatePlayer(playerInfoWithoutStats);
        });
        assertEquals(Errors.PLAYER_STATISTICS_ARE_EMPTY.getMessage(), exception.getMessage());
    }
}

