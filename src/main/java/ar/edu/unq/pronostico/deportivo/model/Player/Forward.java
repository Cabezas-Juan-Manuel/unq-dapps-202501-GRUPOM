package ar.edu.unq.pronostico.deportivo.model.Player;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
@Getter
@Setter
public class Forward extends Player {
    private Map<String, String> performanceStatistics;

    public Forward(String name, String age, String team, String nationality, Map<String, String> perfomanceStatistics){
        super(name, age, team, nationality);
        this.performanceStatistics = perfomanceStatistics;
    }

    @Override
    public Double calculatePerformance() {
        double goals = Double.parseDouble(this.performanceStatistics.get("Goals"));
        double assists = Double.parseDouble(this.performanceStatistics.get("Assists"));
        return (goals / 2) + (assists / 2);
    }
}
