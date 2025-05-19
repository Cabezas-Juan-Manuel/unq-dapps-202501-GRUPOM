package ar.edu.unq.pronostico.deportivo.service;

import ar.edu.unq.pronostico.deportivo.model.PlayerH.Player;
import ar.edu.unq.pronostico.deportivo.model.playerFactory.PlayerGenerator;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PlayerService {

    public Player makePlayerFromData(List<Map<String, String>> playerData) {
        PlayerGenerator playerGenerator = new PlayerGenerator();
        return playerGenerator.generatePlayerWithData(playerData);
    }

    public Double getPerformanceForPlayer(Player player) {
        return player.calculatePerformance();
    }
}
