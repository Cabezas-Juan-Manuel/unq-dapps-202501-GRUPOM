package ar.edu.unq.pronosticoDeportivo.webservice;

import ar.edu.unq.pronosticoDeportivo.model.Player;
import ar.edu.unq.pronosticoDeportivo.service.integration.WhoScoredService;
import ar.edu.unq.pronosticoDeportivo.utils.JsonParser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pronosticoDeportivo")
public class PronosticoDeportivoController {

    @GetMapping("/team/{teamName}/players")
    public ResponseEntity<List<Player>> getTeamPlayers(@PathVariable String teamName) {
        System.out.println(teamName);
        String jsonString = WhoScoredService.getDataFromTableOnWeb(teamName, "team", "team-players");
        List<Player> players = JsonParser.fromJsonToPlayerList(jsonString);
        return ResponseEntity.ok().body(players);
    }
}
