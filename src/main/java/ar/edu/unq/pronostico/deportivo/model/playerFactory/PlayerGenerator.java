package ar.edu.unq.pronostico.deportivo.model.playerFactory;

import ar.edu.unq.pronostico.deportivo.model.player.Player;
import ar.edu.unq.pronostico.deportivo.errors.Errors;
import lombok.Getter;

import java.util.*;
@Getter
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
        checkIfpositionIsValid(position);
        PlayerFactory factory = factoryMap.get(position);
        return factory.generatePlayer(playerData);
    }

    private String extractPrimaryPosition(List<Map<String, String>> playerInfoAndStatsData) {

        ArrayList<String> validPositions = new ArrayList<>(Arrays.asList("Forward", "Midfielder", "Defender", "Goalkeeper"));

        Map<String, String> playerInfo = playerInfoAndStatsData.getFirst();
        checkIfTheresAPosition(playerInfo);
        String mainPositionsOfPlayer = playerInfo.get("Positions:");
        int i = 0;
        while (i < validPositions.size()) {
            String position = validPositions.get(i);
            if (mainPositionsOfPlayer.contains(position)) {
                return position;
            }
            i++;
        }

        return null;
    }

    private void checkIfpositionIsValid(String position) {
        if(position == null){
            throw  new NullPointerException(Errors.POSITION_DOES_NOT_MATCH.getMessage());
        }
    }

    private void checkIfTheresAPosition(Map<String, String> playerInfo) {
        if(!playerInfo.containsKey("Positions:")) {
            throw new IllegalArgumentException(Errors.THERES_NO_POSITION_AVAILABLE_FOR_THIS_PLAYER.getMessage());
        }
    }


}
