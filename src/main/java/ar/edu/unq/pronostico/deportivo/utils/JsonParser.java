package ar.edu.unq.pronostico.deportivo.utils;

import ar.edu.unq.pronostico.deportivo.model.Player;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

public class JsonParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private JsonParser(){
    }

    public static List<Player> fromJsonToPlayerList(String json) {
        // Deserializar el JSON como JsonNode
        List<Player> playerList = new ArrayList<>();

        try {
            JsonNode rootNode = objectMapper.readTree(json);

            // Recorrer el JsonNode y extraer solo los campos deseados
            for (JsonNode node : rootNode) {
                Player player = new Player();
                player.setName(node.get("Player").asText());
                player.setMatchesPlayed(node.get("Apps").asInt());
                player.setGoals(node.get("Goals").asInt());
                player.setAssist(node.get("Assists").asInt());
                player.setRating(node.get("Rating").asDouble());
                playerList.add(player);
            }
        } catch (JsonProcessingException e) {
            AppLogger.error("JsonParser", e.getMessage(), "Error to process json");
        } catch (Exception e) {
            AppLogger.error("JsonParser", e.getMessage(), "Unknown error");
        }

        return playerList;
    }

    public static Player fromJsonToPlayer(String jsonString) {
        Player player = new Player();
        try {
            JsonNode rootNode = objectMapper.readTree(jsonString);
            for (JsonNode node : rootNode) {
                player.setName(node.get("Player").asText());
                player.setMatchesPlayed(node.get("Apps").asInt());
                player.setGoals(node.get("Goals").asInt());
                player.setAssist(node.get("Assists").asInt());
                player.setRating(node.get("Rating").asDouble());
                return player;
            }

        } catch (JsonProcessingException e) {
            AppLogger.error("JsonParser", e.getMessage(), "Error to process json");
        }
        return player;
    }
}
