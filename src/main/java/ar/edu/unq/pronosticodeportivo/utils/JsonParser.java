package ar.edu.unq.pronosticodeportivo.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import ar.edu.unq.pronosticodeportivo.model.Player;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

public class JsonParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();

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
}
