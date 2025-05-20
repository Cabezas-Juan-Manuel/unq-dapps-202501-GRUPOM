package ar.edu.unq.pronostico.deportivo.model.playerFactory;

import ar.edu.unq.pronostico.deportivo.model.Player.Player;

import java.util.*;
public class PlayerGenerator {
    private Map<String, PlayerFactory> factoryMap = new HashMap<>();

    public void playerFactoryRegistry() {
        factoryMap.put("Forward", new ForwardFactory());
        factoryMap.put("Midfielder", new MidFielderFactory());
        factoryMap.put("Defender", new DefenderFactory());
        factoryMap.put("Goalkeeper", new GoalkeeperFactory());
    }

    public PlayerGenerator(){
        playerFactoryRegistry();
    }

    public Player generatePlayerWithData(List<Map<String, String>> playerData) {
        String position = extractPrimaryPosition(playerData);
        PlayerFactory factory = factoryMap.get(position);
        return factory.generatePlayer(playerData);
    }

    private String extractPrimaryPosition(List<Map<String, String>> playerInfoAndStatsData) {

        ArrayList<String> validPositions = new ArrayList<>(Arrays.asList("Forward", "Midfielder", "Defender", "Goalkeeper"));

        Map<String, String> playerInfo = playerInfoAndStatsData.getFirst();
        String mainPositionsOfPlayer = playerInfo.get("Positions:");

        int i = 0;
        while (i < validPositions.size()) {
            String position = validPositions.get(i);
            if (mainPositionsOfPlayer.contains(position)) {
                return position;
            }
            i++;
        }

        return mainPositionsOfPlayer; // Que tire error o devuelva un null y que tire error el service
    }


}
