package ar.edu.unq.pronostico.deportivo.model.playerFactory;

import ar.edu.unq.pronostico.deportivo.model.Player.Player;

import java.util.List;
import java.util.Map;

public interface IPlayerFactory {
    public Player generatePlayer(List<Map<String, String>> playerInfoAndStatistics);
}
