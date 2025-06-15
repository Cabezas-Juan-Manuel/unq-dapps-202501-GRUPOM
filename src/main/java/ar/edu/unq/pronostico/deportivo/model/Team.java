package ar.edu.unq.pronostico.deportivo.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@Getter
@Setter
public class Team {
    private String name;
    private Map<String, String> defensiveStats;
    private Map<String, String> offensiveStats;

    public Team(String teamName, List<Map<String, String>> teamStats) {
        this.name = teamName;
        this.offensiveStats = teamStats.get(0);
        this.defensiveStats = teamStats.get(1);
    }

    public String getShotsReceived() {
       return  this.defensiveStats.get("Shots pg");
    }

    public String getinterceptions() {
        return this.defensiveStats.get("Interceptions pg");
    }

    public String getfoulsMade() {
        return this.defensiveStats.get("Fouls pg");
    }

    public String getShotsMade() {
        return this.offensiveStats.get("Shots pg");
    }

    public String getDribbles() {
        return this.offensiveStats.get("Dribbles pg");
    }

    public String getFoulsRecievedPerGame() {
        return this.offensiveStats.get("Fouled pg");
    }
}
