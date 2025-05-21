package ar.edu.unq.pronostico.deportivo.model.Player;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
@Getter
@Setter
public class Goalkeeper extends Player {
    private Map<String, String> performanceStatistics;

    public Goalkeeper(String name, String age, String team, String nationality, Map<String, String> perfomanceStatistics){
        super(name, age, team, nationality);
        this.performanceStatistics = perfomanceStatistics;
    }

    @Override
    public Double calculatePerformance() {
        double clear = Double.parseDouble(this.performanceStatistics.get("Clear"));
        double blocks = Double.parseDouble(this.performanceStatistics.get("Blocks"));
        double fouls = Double.parseDouble(this.performanceStatistics.get("Fouls"));
        return clear + blocks - fouls;
    }
}
