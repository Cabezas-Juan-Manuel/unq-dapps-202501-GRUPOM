package ar.edu.unq.pronostico.deportivo.utils;

import ar.edu.unq.pronostico.deportivo.model.PlayerForTeam;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

public class JsonParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String JSON_PARSER_STRING = "JsonParser";

    private JsonParser(){
    }

    public static List<PlayerForTeam> fromJsonToPlayerList(String json) {
        // Deserializar el JSON como JsonNode
        List<PlayerForTeam> playerList = new ArrayList<>();

        try {
            JsonNode rootNode = objectMapper.readTree(json);

            // Recorrer el JsonNode y extraer solo los campos deseados
            for (JsonNode node : rootNode) {
                PlayerForTeam player = new PlayerForTeam();
                player.setName(node.get("Player").asText());
                player.setMatchesPlayed(node.get("Apps").asInt());
                player.setGoals(node.get("Goals").asInt());
                player.setAssist(node.get("Assists").asInt());
                player.setRating(node.get("Rating").asDouble());
                playerList.add(player);
            }
        } catch (JsonProcessingException e) {
            AppLogger.error(JSON_PARSER_STRING, e.getMessage(), "Error to process json");
        } catch (Exception e) {
            AppLogger.error(JSON_PARSER_STRING, e.getMessage(), "Unknown error");
        }

        return playerList;
    }
}
